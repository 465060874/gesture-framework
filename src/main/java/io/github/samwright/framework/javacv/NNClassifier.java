package io.github.samwright.framework.javacv;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.datatypes.Classification;
import io.github.samwright.framework.model.datatypes.Features;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.History;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_ml.CvANN_MLP;
import static com.googlecode.javacv.cpp.opencv_ml.CvANN_MLP_TrainParams;

/**
 * Adapted from: http://projectagv.blogspot.co.uk/2008/12/sample-code-for-cvannmlp.html
 */
public class NNClassifier extends AbstractElement {

    private Map<History,CvANN_MLP> nets = new HashMap<>();
    private Map<History, List<Features>> trainingSet = new HashMap<>();
    private int featuresSize = -1;
    private Set<String> classSet = new HashSet<>();
    private List<String> classIndex = new ArrayList<>();

    public NNClassifier() {
        super(new TypeData(Features.class, Classification.class));
    }

    public NNClassifier(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Features features = (Features) input.getData();
        return input.createNext(this, new Classification(predict(features, input.getHistory())));
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        Features features = (Features) input.getData();

        if (trainingSet.isEmpty()) {
            featuresSize = features.getFeatures().size();
        } else if (features.getFeatures().size() != featuresSize) {
            throw new RuntimeException("Each training data must have same number of features!");
        }

        classSet.add(features.getTag());

        List<Features> featuresList = trainingSet.get(input.getHistory());
        if (featuresList == null) {
            featuresList = new ArrayList<>();
            trainingSet.put(input.getHistory(), featuresList);
        }
        featuresList.add(features);

        return Arrays.asList(input.createNext(this, new Classification(features.getTag())));
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        // Set up the class index
        classIndex.clear();
        classIndex.addAll(classSet);

        // Setup a network for each unique mediator history:
        for (Map.Entry<History,List<Features>> e : trainingSet.entrySet()) {
            History history = e.getKey();
            List<Features> featuresList = e.getValue();

            // Create new or load existing net
            CvANN_MLP net = nets.get(history);
            if (net == null) {
                net = createNet();
                nets.put(history, net);
            }

            // Train net
            trainLeavingOneOut(net, featuresList, history);
        }

        System.out.println("Nets: " + nets);

        Set<Mediator> successful = new HashSet<>();

        for (Mediator mediator : completedTrainingBatch.getAll()) {
            Mediator inputMediator = mediator.getPrevious();
            Features features = (Features) inputMediator.getData();

            if (predict(features, inputMediator.getHistory()).equals(features.getTag()))
                successful.add(mediator.getPrevious());
        }

        Set<Mediator> all = Mediator.rollbackMediators(completedTrainingBatch.getAll());
        return new CompletedTrainingBatch(all, successful);
    }

    private String predict(Features features, History history) {
        // Setup the features in the correct format
        CvMat inputData = cvCreateMat(1, featuresSize, CV_32FC1);

        for (int i = 0; i < featuresSize; ++i)
            cvSetReal2D(inputData, 0, i, features.getFeatures().get(i));

        // Prepare output variable
        CvMat outputData = cvCreateMat(1, classIndex.size(), CV_32FC1);

        // Predict, and populate outputData.
        nets.get(history).predict(inputData, outputData);

        // Find index of largest output:
        double maxVal = -100;
        int maxIndex = -1;
        for (int i = 0; i < classIndex.size(); ++i) {
            double classOutput = cvGet2D(outputData, 0, i).getDoublePointerVal().get();
            if (classOutput > maxVal) {
                maxVal = classOutput;
                maxIndex = i;
            }
        }

        return classIndex.get(maxIndex);
    }

    private void trainLeavingOneOut(CvANN_MLP net, List<Features> trainingSet, History history) {
        LinkedList<Features> partialTrainingSet = new LinkedList<>(trainingSet);
        int successes = 0;

        for (int i = 0; i < trainingSet.size(); ++i) {
            Features removed = partialTrainingSet.removeFirst();

            trainNet(net, partialTrainingSet);
            if (predict(removed, history).equals(removed.getTag()))
                ++successes;

            partialTrainingSet.addLast(removed);
        }

        System.out.format("Success rate = %f (%d out of %d)%n",
                (successes * 100. / trainingSet.size()), successes, trainingSet.size());
    }

    private CvANN_MLP createNet() {
        // Setup NN layers information
        CvMat layers = cvCreateMat(4, 1, CV_32SC1);
        cvSetReal1D(layers, 0, featuresSize);
        cvSetReal1D(layers, 1, 15);
        cvSetReal1D(layers, 2, 15);
        cvSetReal1D(layers, 3, classSet.size());

        // Initialise net
        CvANN_MLP net = new CvANN_MLP();
        net.create(layers, CvANN_MLP.SIGMOID_SYM, 1., 1.);

        return net;
    }

    private void trainNet(CvANN_MLP net, List<Features> trainingSet) {
        // Create matrix of training set
        CvMat trainingData = cvCreateMat(trainingSet.size(), featuresSize, CV_32FC1);

        // Create matrix of classifications
        CvMat classificationData = cvCreateMat(trainingSet.size(), classSet.size(), CV_32FC1);

        // Create training data weighting matrix
        CvMat trainingWeights = cvCreateMat(trainingSet.size(), 1, CV_32FC1);

        // Fill in the matrices
        for (int i = 0; i < trainingSet.size(); ++i) {
            Features features = trainingSet.get(i);

            // Training data:
            for (int j = 0; j < featuresSize; ++j)
                cvSetReal2D(trainingData, i, j, features.getFeatures().get(j));

            // Classification:
            for (int j = 0; j < classIndex.size(); ++j) {
                double isCorrectClass = classIndex.get(j).equals(features.getTag()) ? 1. : -1.;
                cvSetReal2D(classificationData, i, j, isCorrectClass);
            }

            // Weights (all set to 1)
            cvSetReal1D(trainingWeights, i, 1.);
        }

        net.train(trainingData, classificationData, trainingWeights, null,
                new CvANN_MLP_TrainParams(), 0);
    }

    @Override
    public Element createMutableClone() {
        return new NNClassifier(this);
    }

    @Override
    public boolean isValid() {
        return !nets.isEmpty();
    }
}

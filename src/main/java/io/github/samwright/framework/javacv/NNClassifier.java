package io.github.samwright.framework.javacv;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.datatypes.Classification;
import io.github.samwright.framework.model.datatypes.Features;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.History;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_ml.CvANN_MLP;
import static com.googlecode.javacv.cpp.opencv_ml.CvANN_MLP_TrainParams;

/**
 * Adapted from: http://projectagv.blogspot.co.uk/2008/12/sample-code-for-cvannmlp.html
 */
public class NNClassifier extends AbstractElement {

    private Map<History,CvANN_MLP> nets = new HashMap<>();
    private int featuresSize = -1;
    private List<String> classIndex = new ArrayList<>();
    @Getter private Map<History,Double> successRates;

    public NNClassifier() {
        super(new TypeData(Features.class, Classification.class));
    }

    public NNClassifier(NNClassifier oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Features features = (Features) input.getData();
        CvANN_MLP net = nets.get(input.getHistory());
        String predictedTag = predict(net, features);

        return input.createNext(this, new Classification(predictedTag));
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        Features features = (Features) input.getData();

        // For training data, assume the classifier works 100% of the time (ie. use the supplied
        // tag as the predicted tag.
        return Arrays.asList(input.createNext(this, new Classification(features.getTag())));
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        // Clean up
        classIndex.clear();
        featuresSize = -1;
        classIndex.clear();
        successRates = null;
        nets.clear();
        Map<History, List<Features>> trainingSet = new HashMap<>();
        Set<String> classSet = new HashSet<>();

        // Make sure there's at least 1 output mediator
        if (completedTrainingBatch.getAll().isEmpty())
            return completedTrainingBatch;

        // Roll back the batch
        CompletedTrainingBatch inputBatch = super.processCompletedTrainingBatch(completedTrainingBatch);

        // Extract data from completed training batch
        for (Mediator input : inputBatch.getAll()) {
            Features features = (Features) input.getData();

            // Check featuresSize is equal across all features
            if (featuresSize == -1)
                featuresSize = features.getFeatures().size();
            else if (featuresSize != features.getFeatures().size())
                throw new RuntimeException("Each training data must have same number of features!");

            // Add tag if not already in classSet
            classSet.add(features.getTag());

            List<Features> featuresList = trainingSet.get(input.getHistory());
            if (featuresList == null) {
                featuresList = new ArrayList<>();
                trainingSet.put(input.getHistory(), featuresList);
            }
            featuresList.add(features);
        }

        classIndex.addAll(classSet);
        successRates = new HashMap<>();

        // Setup a network for each unique mediator history:
        for (Map.Entry<History,List<Features>> e : trainingSet.entrySet()) {
            History history = e.getKey();
            List<Features> featuresList = e.getValue();

            // Create new or load existing net
            CvANN_MLP net = createNet(featuresSize, classIndex.size());
            nets.put(history, net);

            // Train net
            double successRate = trainLeavingOneOut(net, featuresList);

            successRates.put(history, successRate);
        }

        Set<Mediator> successful = new HashSet<>();

        for (Mediator inputMediator : inputBatch.getSuccessful()) {
            Features features = (Features) inputMediator.getData();
            CvANN_MLP net = nets.get(inputMediator.getHistory());

            if (predict(net, features).equals(features.getTag()))
                successful.add(inputMediator);
        }

        return new CompletedTrainingBatch(inputBatch.getAll(), successful);
    }

    private String predict(CvANN_MLP net, Features features) {
        if (!isValid())
            return "Not trained";
        // Setup the features in the correct format
        CvMat inputData = cvCreateMat(1, featuresSize, CV_32FC1);

        for (int i = 0; i < featuresSize; ++i)
            cvSetReal2D(inputData, 0, i, features.getFeatures().get(i));

        // Prepare output variable
        CvMat outputData = cvCreateMat(1, classIndex.size(), CV_32FC1);

        // Predict, and populate outputData.
        net.predict(inputData, outputData);

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

    /**
     * Trains the given net on the given training set using the leave-1-out strategy,
     * returning the average training success rate as a fraction (ie. 0 -> 1).
     *
     * @param net the net to train.
     * @param trainingSet the training set to train the net with.
     * @return the success rate as a fraction (ie. 0 -> 1).
     */
    private double trainLeavingOneOut(CvANN_MLP net, List<Features> trainingSet) {
        LinkedList<Features> partialTrainingSet = new LinkedList<>(trainingSet);
        int successes = 0;

        for (int i = 0; i < trainingSet.size(); ++i) {
            Features removed = partialTrainingSet.removeFirst();

            trainNet(net, partialTrainingSet);
            if (predict(net, removed).equals(removed.getTag()))
                ++successes;

            partialTrainingSet.addLast(removed);
        }

        try {
            return successes * 1. / trainingSet.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Creates a net with the given features size and classes size (for input and output layer
     * sizes respectively).
     *
     * @param featuresSize the number of features.
     * @param classesSize the number of classes.
     * @return the new net.
     */
    private CvANN_MLP createNet(int featuresSize, int classesSize) {
        // Setup NN layers information
        CvMat layers = cvCreateMat(4, 1, CV_32SC1);
        cvSetReal1D(layers, 0, featuresSize);
        cvSetReal1D(layers, 1, 15);
        cvSetReal1D(layers, 2, 15);
        cvSetReal1D(layers, 3, classesSize);

        // Initialise net
        CvANN_MLP net = new CvANN_MLP();
        net.create(layers, CvANN_MLP.SIGMOID_SYM, 1., 1.);

        return net;
    }

    /**
     * Train the given net with the given training set.
     *
     * @param net the net to train.
     * @param trainingSet the training set to train the net with.
     */
    private void trainNet(CvANN_MLP net, List<Features> trainingSet) {
        // Create matrix of training set
        CvMat trainingData = cvCreateMat(trainingSet.size(), featuresSize, CV_32FC1);

        // Create matrix of classifications
        CvMat classificationData = cvCreateMat(trainingSet.size(), classIndex.size(), CV_32FC1);

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

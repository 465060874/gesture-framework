package attempt2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:33
 */
public abstract class TrainablePerHistory {
    private final Map<History, Classifier> classifiers = new HashMap<>();

    public void train(History history, Set<TrainingSample> trainingSet) {
        Classifier classifier = classifiers.get(history);
        if (classifier == null) {
            classifier = createClassifier();
            classifiers.put(history, classifier);
        }

        for (TrainingSample sample : trainingSet)
            classifier.train(sample);

    }

    public abstract Classifier createClassifier();
}

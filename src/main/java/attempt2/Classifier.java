package attempt2;

import java.util.Collection;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:49
 */
public interface Classifier {
    void train(Collection<TrainingSample> trainingSamples, Collection<TrainingSample> testSamples);

    void trainLeaveOneOut(Collection<TrainingSample> samples);

    double getTestSuccess();

    int classify(double[] features);
}

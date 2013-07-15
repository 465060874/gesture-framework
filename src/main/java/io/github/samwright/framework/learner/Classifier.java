package io.github.samwright.framework.learner;

import java.util.Collection;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:49
 */
public interface Classifier {

    void train(Collection<TrainingSample> samples);

    double getSuccessRate();

    int classify(double[] features);
}

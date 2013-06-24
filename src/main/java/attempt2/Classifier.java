package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:49
 */
public interface Classifier {
    void train(TrainingSample trainingSample);

    int classify(double[] features);
}

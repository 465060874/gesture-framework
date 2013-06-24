package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:44
 */
public class TrainingSample {
    private final double[] features;
    private final double[] classValues;


    public TrainingSample(double[] features, double[] classValues) {
        this.features = features;
        this.classValues = classValues;
    }

    public double[] getFeatures() {
        return features;
    }

    public double[] getClassValues() {
        return classValues;
    }
}

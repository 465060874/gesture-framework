package attempt2;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 08:12
 */
public class TrainingResult<T> {
    private final T input;
    private final boolean correctlyClassified;

    public TrainingResult(T input, boolean correctlyClassified) {
        this.input = input;
        this.correctlyClassified = correctlyClassified;
    }

    public boolean isCorrectlyClassified() {
        return correctlyClassified;
    }

    public T getInput() {
        return input;
    }
}

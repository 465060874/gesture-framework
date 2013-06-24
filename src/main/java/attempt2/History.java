package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 12:31
 */
public class History {
    private final History previous;
    private final Processor<?,?> creator;

    private History(History previous, Processor<?, ?> creator) {
        this.previous = previous;
        this.creator = creator;
    }

    public History getPrevious() {
        return previous;
    }

    public Processor<?, ?> getCreator() {
        return creator;
    }

    public History createNext(Processor<?, ?> creator) {
        return new History(this, creator);
    }

    public boolean isEmpty() {
        return previous == null;
    }

    public static History createEmpty() {
        return new History(null, null);
    }
}

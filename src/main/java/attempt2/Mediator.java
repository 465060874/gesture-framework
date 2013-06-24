package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 12:26
 */
public class Mediator<T> {
    private final T data;
    private final History history;
    private final Mediator<?> previous;

    public <U> Mediator<U> createNext(Processor<?,?> creator, U data) {
        History nextHistory = history.createNext(creator);
        return new Mediator<>(data, nextHistory, this);
    }

    public static <U> Mediator<U> createEmpty() {
        return new Mediator<>(null, History.getEpoch(), null);
    }

    private Mediator(T data, History history, Mediator<?> previous) {
        this.data = data;
        this.history = history;
        this.previous = previous;
    }

    public boolean isEmpty() {
        return previous == null;
    }

    public T getData() {
        return data;
    }

    public History getHistory() {
        return history;
    }

    public Mediator<?> getPrevious() {
        return previous;
    }

    public Mediator<?> getAncestorCreatedBy(Processor<?,?> creator) {
        Mediator<?> mediator = this;

        while(mediator != null && mediator.getHistory().getCreator() != creator)
            mediator = mediator.getPrevious();


        return mediator;
    }
}

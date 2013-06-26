package BestSoFar.framework.helper;

import BestSoFar.framework.core.Processor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Implementation of Mediator.  Don't use this - create via Mediator instead.
 */
@AllArgsConstructor
public class MediatorImpl<T> extends Mediator<T> {
    @Getter @NonNull
    private final T data;
    @Getter @NonNull private final History history;
    @Getter private final Mediator<?> previous;

    public static Mediator<?> createEmpty() {
        return new MediatorImpl<>(null, History.getEpoch(), null);
    }

    public <U> Mediator<U> createNext(Processor<?, ?> creator, U data) {
        return new MediatorImpl<>(data, history.createNext(creator), this);
    }

    public boolean isEmpty() {
        return previous == null;
    }

    public Mediator<?> getAncestorCreatedBy(Processor<?, ?> creator) {
        Mediator<?> mediator = this;

        while (mediator != null && mediator.getHistory().getCreator() != creator)
            mediator = mediator.getPrevious();


        return mediator;
    }
}

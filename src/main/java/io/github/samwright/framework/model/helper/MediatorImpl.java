package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Implementation of {@link Mediator}.  Don't use this - create via {@code Mediator} instead.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
final public class MediatorImpl extends Mediator {
    @Getter private final Object data;
    @Getter @NonNull private final History history;
    @Getter private final Mediator previous;

    public static Mediator createEmpty() {
        return new MediatorImpl(null, History.getEpoch(), null);
    }

    public Mediator createNext(@NonNull Processor creator, @NonNull Object data) {
        return new MediatorImpl(data, history.createNext(creator), this);
    }

    public boolean isEmpty() {
        return previous == null;
    }
}

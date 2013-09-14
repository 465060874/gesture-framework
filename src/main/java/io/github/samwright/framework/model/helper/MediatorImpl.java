package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Mediator join(@NonNull Processor creator, @NonNull List<Mediator> mediatorsToJoin) {
        Set<History> joinedHistories = new HashSet<>();
        for (Mediator mediatorToJoin : mediatorsToJoin)
            joinedHistories.add(mediatorToJoin.getHistory());

        return new MediatorImpl(mediatorsToJoin, history.join(creator, joinedHistories), this);
    }

    public boolean isEmpty() {
        return previous == null;
    }
}

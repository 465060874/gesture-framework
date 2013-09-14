package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link History}.  Don't use this class - access it via {@code History}.
 */
final public class HistoryImpl extends History {
    @Getter private final static History epoch = new HistoryImpl(null, null);

    @Getter private final History previous;
    @Getter private final Processor creator;
    private final Map<Processor, History> nextByCreator = new HashMap<>();
    private final Map<Set<History>, History> nextByJoinedHistory = new HashMap<>();
    @Getter private final Set<History> joined;

    private HistoryImpl(History previous, Processor creator) {
        this.previous = previous;
        this.creator = creator;
        joined = null;
    }

    private HistoryImpl(History previous, Processor creator, Set<History> toJoin) {
        this.previous = previous;
        this.creator = creator;
        joined = Collections.unmodifiableSet(toJoin);
    }

    @Override
    public History createNext(@NonNull Processor creator) {
        History next = nextByCreator.get(creator);

        if (next == null) {
            next = new HistoryImpl(this, creator);
            nextByCreator.put(creator, next);
        }

        return next;
    }

    @Override
    public History join(@NonNull Processor creator, @NonNull Set<History> toJoin) {
        History next = nextByJoinedHistory.get(toJoin);

        if (next == null) {
            next = new HistoryImpl(this, creator, toJoin);
            nextByJoinedHistory.put(toJoin, next);
        }

        return next;
    }

    @Override
    public boolean isJoinPoint() {
        return joined != null;
    }

    @Override
    public void discardFutureFrom(@NonNull Processor creator) {
        nextByCreator.remove(creator);
    }
}

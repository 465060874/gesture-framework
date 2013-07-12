package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link History}.  Don't use this class - access it via {@code History}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final public class HistoryImpl extends History {
    @Getter private final static History epoch = new HistoryImpl(null, null);

    @Getter private final History previous;
    @Getter private final Processor<?, ?> creator;
    private final Map<Processor<?, ?>, History> nextByCreator = new HashMap<>();

    @Override
    public History createNext(@NonNull Processor<?, ?> creator) {
        History next = nextByCreator.get(creator);

        if (next == null) {
            next = new HistoryImpl(this, creator);
            nextByCreator.put(creator, next);
        }

        return next;
    }

    @Override
    public void discardFutureFrom(@NonNull Processor<?, ?> creator) {
        nextByCreator.remove(creator);
    }
}

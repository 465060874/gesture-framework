package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link History}.  Don't use this class - access it via {@code History}.
 */
@RequiredArgsConstructor
final public class HistoryImpl extends History {
    @Getter
    private final History previous;
    @Getter
    private final Processor<?, ?> creator;

    private final Map<Processor<?, ?>, History> nextByCreator = new HashMap<>();

    public History createNext(Processor<?, ?> creator) {
        History next = nextByCreator.get(creator);

        if (next == null) {
            next = new HistoryImpl(this, creator);
            nextByCreator.put(creator, next);
        }

        return next;
    }
}

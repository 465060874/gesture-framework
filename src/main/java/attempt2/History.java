package attempt2;

import java.util.*;

/**
 * Each history object represents a unique sequence of Processors - ie. no two histories
 * share the same sequence of Processors (without being the same object).
 *
 * Each object contains a reference to the history object that came immediately before it,
 * and a list of references to the history objects which have come after it.
 *
 * Each history begins with the same epoch.  To create a new history:
 *
 * History newHistory = History.getEpoch().createNext(creator);
 */
public class History {
    private final History previous;
    private final Map<Processor<?,?>, History> nextByCreator = new HashMap<>();
    private final Processor<?,?> creator;

    private final static History epoch = new History(null, null);

    /**
     * Get the initial (empty) History object from which all history objects originate.
     *
     * @return the epoch (definition: a moment in time defined as the origin of an era).
     */
    public static History getEpoch() {
        return epoch;
    }

    /**
     * Private initialiser - used to create the 'epoch' and in 'createNext()'.
     *
     * @param previous the history object after which this new object is created.
     * @param creator the Processor object that created this new object.
     */
    private History(History previous, Processor<?, ?> creator) {
        this.previous = previous;
        this.creator = creator;
    }

    /**
     * Create a history object immediately after this one, created by 'creator'.
     *
     * If the 'creator' already created a new History object from this one, it is
     * returned instead (ie. h.createNext(p) == h.createNext(p) is always true).
     *
     * @param creator the Processor that created the new object.
     * @return the new History object.
     */
    public History createNext(Processor<?, ?> creator) {
        History next = nextByCreator.get(creator);

        if (next == null) {
            next = new History(this, creator);
            nextByCreator.put(creator, next);
        }

        return next;
    }

    /**
     * Get the History object that immediately preceeded this one.
     *
     * @return the History object that immediately preceeded this one.
     */
    public History getPrevious() {
        return previous;
    }

    /**
     * Get the Processor object that created this history object.
     *
     * @return the Processor object that created this history object.
     */
    public Processor<?, ?> getCreator() {
        return creator;
    }
}

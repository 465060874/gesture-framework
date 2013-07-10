package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;
import lombok.Getter;

/**
 * Each {@code History} object represents a unique sequence of {@link Processor} objects - ie. no
 * two histories share the same sequence of {@code Processor} objects (without being the same
 * object).
 * <p/>
 * Each {@code History} object contains a reference to the {@code History} object that came
 * immediately before it, and a list of references to the {@code History} objects which have come
 * after it.
 * <p/>
 * Each history begins with the same epoch (which is a singleton).  To create a new history:
 * <p/>
 * {@code History newHistory = History.getEpoch().createNext(creator);}
 */
public abstract class History {

    @Getter private final static History epoch = new HistoryImpl(null, null);

    /**
     * Create a history object immediately after this one, created by 'creator'.
     *
     * If the 'creator' already created a new History object from this one, it is
     * returned instead (ie. h.createNext(p) == h.createNext(p) is always true).
     *
     * @param creator the Processor that created the new object.
     * @return the new History object.
     */
    public abstract History createNext(Processor<?, ?> creator);

    /**
     * Get the History object that immediately preceded this one.
     *
     * @return the History object that immediately preceded this one.
     */
    public abstract History getPrevious();

    /**
     * Get the Processor object that created this history object.
     *
     * @return the Processor object that created this history object.
     */
    public abstract Processor<?, ?> getCreator();
}

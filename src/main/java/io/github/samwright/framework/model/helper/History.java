package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;

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

    public static History getEpoch() {
        return HistoryImpl.getEpoch();
    }

    /**
     * Create a {@code History} object immediately after this one, created by 'creator'.
     *
     * If the 'creator' already created a new {@code History} object from this one, it is
     * returned instead (ie. {@code h.createNext(p) == h.createNext(p)} is always true).
     *
     * @param creator the {@code Processor} that created the new object.
     * @return the new {@code History} object.
     */
    public abstract History createNext(Processor<?, ?> creator);

    /**
     * Get the {@code History} object that immediately preceded this one.
     *
     * @return the {@code History} object that immediately preceded this one.
     */
    public abstract History getPrevious();

    /**
     * Get the {@code Processor} object that created this {@code History} object.
     *
     * @return the {@code Processor} object that created this {@code History} object.
     */
    public abstract Processor<?, ?> getCreator();

    /**
     * Discards the {@code History} object created by the {@code creator} from this one.
     *
     * @param creator the {@link Processor} that created the {@code History} object from this one
     *                to be deleted.
     */
    public abstract void discardFutureFrom(Processor<?, ?> creator);
}

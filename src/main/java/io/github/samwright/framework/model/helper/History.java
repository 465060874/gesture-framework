package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;

import java.util.HashSet;
import java.util.Set;

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
    public abstract History createNext(Processor creator);

    /**
     * Create a {@code History} object after this one, which has been branched off and processed
     * concurrently and now needs to be joined together.
     * <p/>
     * The supplied set of {@code History} objects were produced by the final Processors in the
     * branches, and if those same {@code History} objects appear another call to
     * {@code this.join(..)}, the same {@code History} object is returned.
     *
     * @param creator the {@link Processor} that is joining the history objects.
     * @param toJoin the history objects to join.
     * @return the next history object after the join.
     */
    public abstract History join(Processor creator, Set<History> toJoin);

    /**
     * If this object was created by {@code history.join(..)} then it is a join point, and the
     * {@code History} objects that were joined are accessible with {@code history.getJoined()}.
     * <p/>
     * Calling {@code getPrevious()} on the returned {@code History} object yields this object.
     * The branched/joined {@code History} objects are only accessible through the returned object.
     *
     * @return true iff this is a join point of other histories.
     */
    public abstract boolean isJoinPoint();

    /**
     * If this is a join point, this returns the {@code History} objects that were joined.
     * Otherwise it returns null.
     *
     * @return the {@code History} objects that were joined in making this. Otherwise it returns
     *         null.
     */
    public abstract Set<History> getJoined();

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
    public abstract Processor getCreator();

    /**
     * Discards the {@code History} object created by the {@code creator} from this one.
     *
     * @param creator the {@link Processor} that created the {@code History} object from this one
     *                to be deleted.
     */
    public abstract void discardFutureFrom(Processor creator);

    public static Set<Processor> getAllCreators(History history) {
        Set<Processor> allCreators = new HashSet<>();
        addCreatorsToSet(history, allCreators);
        return allCreators;
    }

    private static void addCreatorsToSet(History history, Set<Processor> creators) {
        if (history == getEpoch())
            return;

        creators.add(history.getCreator());

        if (history.isJoinPoint()) {
            for (History joined : history.getJoined())
                addCreatorsToSet(joined, creators);
        }

        addCreatorsToSet(history.getPrevious(), creators);
    }
}

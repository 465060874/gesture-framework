package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;

import java.util.HashSet;
import java.util.Set;

/**
 * Mediator objects contain data that was produced from a {@link Processor} object, and can
 * be fed into another {@code Processor} object.
 * <p/>
 * Each {@code Mediator} has a link to the {@code Mediator} object which was fed into the
 * {@code Processor} that created it (ie. the 'previous' {@code Mediator}).  The first
 * {@code Mediator} in this linked list is an 'empty' {@code Mediator} (created using
 * {@code Mediator.createEmpty()}) where both the data contained in it and the previous
 * {@code Mediator} are {@code null}, and the {@link History} is {@code History.getEpoch()}.
 * <p/>
 * Each {@code Mediator} also has a {@code History} object which represents the list of
 * {@code Processor} objects which created it.  Two {@code Mediator} objects which were created by
 * the exact same procession of {@code Processor} objects will share the same {@code History}
 * object (ie. {@code mediator1.getHistory() == mediator2.getHistory())} regardless of whether the
 * {@code Mediator} objects' data are the same or not.
 */
public abstract class Mediator {

    /**
     * Creates an empty {@code Mediator}, which is used as the starting element from which future
     * {@code Mediator} objects (which actually contain data) are created.  Its 'data' and
     * 'previous' variables are null, and its {@link History} object is set at the epoch (ie.
     * {@code History.getEpoch()}).
     *
     * @return an empty {@code Mediator}, from which {@code Mediator} objects (which contain data)
     *         can be created.
     */
    public static Mediator createEmpty() {
        return MediatorImpl.createEmpty();
    }

    /**
     * Create the output {@code Mediator} from this input {@code Mediator}, given the newly-created
     * 'data'.
     *
     * @param creator the {@link Processor} that used this input {@code Mediator} to produce the
     *                supplied data.
     * @param data the data the {@code Processor} created from this input {@code Mediator}.
     * @return an output {@code Mediator} containing 'data' created by 'creator' using this
     *         {@code Mediator} as input.
     */
    public abstract Mediator createNext(Processor creator, Object data);

    /**
     * Returns true iff this is an empty {@code Mediator} (ie. the first in a sequence of
     * {@code Mediator} objects).
     *
     * @return true iff this is an empty {@code Mediator} (ie. the first in a sequence of
     *         {@code Mediator} objects).
     */
    public abstract boolean isEmpty();

    /**
     * Gets the data contained in this {@code Mediator} object.
     *
     * @return the data contained in this {@code Mediator} object.
     */
    public abstract Object getData();

    /**
     * Gets the {@link History} object (ie. the sequence of {@link Processor} which led to this
     * {@code Mediator} object's creation).
     *
     * @return the {@code History} object for this {@code Mediator}.
     */
    public abstract History getHistory();

    /**
     * Gets the {@code Mediator} object from which this one was created.
     *
     * @return the {@code Mediator} object from which this one was created.
     */
    public abstract Mediator getPrevious();

    /**
     * For every {@code Mediator m} in {@code mediators}, this returns {@code m.getPrevious()} in
     * a set.
     * <p/>
     * NB. Since duplicates are not allowed in a set, if any {@code Mediator} objects in
     * {@code mediators} share the same previous {@code Mediator}, it will appear only once in
     * the returned set.
     *
     * @param mediators the {@code Mediator} objects to roll back to their previous
     *                  {@code Mediator} objects.
     * @return the {@code Mediator} objects that preceded the supplied {@code mediators} set.
     */
    public static Set<Mediator> rollbackMediators(Set<Mediator> mediators) {
        Set<Mediator> previousMediators = new HashSet<>();
        for (Mediator mediator : mediators)
            previousMediators.add(mediator.getPrevious());

        return previousMediators;
    }
}

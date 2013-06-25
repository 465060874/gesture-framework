package BestSoFar.framework.helper;

import BestSoFar.framework.abstractions.Processor;

/**
 * Mediator objects contain data that was produced from a Processor<?,T> object, and can
 * be fed into another Processor<T,?> object.
 *
 * Each mediator has a link to the mediator object which was fed into the Processor that
 * created it (ie. the 'previous' mediator).  The first mediator in this linked list is
 * an 'empty' mediator (created using Mediator.createEmpty()) where both the data contained in it
 * and the previous Mediator are null, and the history is 'History.getEpoch()'.
 *
 * Each mediator also has a history object which represents the list of Processor objects
 * which created it.  Two mediator objects which were created by the exact same procession
 * of Processors will share the same History object
 * (ie. mediator1.getHistory() == mediator2.getHistory()) regardless of whether the mediators'
 * data are the same or not.
 */
public abstract class Mediator<T> {

    /**
     * Creates an empty mediator, which is used as the starting element from which future mediators
     * (which actually contain data) are created.  Its 'data' and 'previous' variables are null, and
     * its history is set at the epoch (ie. History.getEpoch()).
     *
     * @return an empty mediator, from which mediators (which contain data) can be created.
     */
    public static Mediator<?> createEmpty() {
        return MediatorImpl.createEmpty();
    }

    /**
     * Create the output mediator from this input mediator, given the newly-created 'data'.
     *
     * @param creator the Processor that used this input mediator to produce 'data'
     * @param data the data the Processor created from this input mediator.
     * @param <U> the type of 'data'.
     * @return an output mediator containing 'data' created by 'creator' using this mediator as input.
     */
    public abstract <U> Mediator<U> createNext(Processor<?,?> creator, U data);

    /**
     * Returns true iff this is an empty mediator (ie. the first in a sequence of mediators).
     *
     * @return true iff this is an empty mediator (ie. the first in a sequence of mediators).
     */
    public abstract boolean isEmpty();

    /**
     * Gets the data contained in this mediator object.
     *
     * @return the data contained in this mediator object.
     */
    public abstract T getData();

    /**
     * Gets the history object (ie. the sequence of Processors which led to this mediator's creation).
     *
     * @return the history object.
     */
    public abstract History getHistory();

    /**
     * Gets the mediator object from which this one was created.
     *
     * @return the mediator object from which this one was created.s
     */
    public abstract Mediator<?> getPrevious();

    /**
     * Gets this mediator's ancestor that was created by 'creator'.
     *
     * If 'creator' created none of this mediator's ancestors, this returns null.
     *
     * TODO: delete this if not needed.
     *
     * @param creator the creator of the ancestral mediator to return.
     * @return the ancestral mediator created by 'creator'.
     */
    public abstract Mediator<?> getAncestorCreatedBy(Processor<?, ?> creator);
}

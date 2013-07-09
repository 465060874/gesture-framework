package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;

import java.util.*;

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

    /**
     * Creates a backward mapping (ie. linking each mediator 'm' against 'm.getPrevious') where each output mediator
     * came from a unique input mediator.  If any two output mediators came from the same input mediator,
     * this will throw an AssertionError.
     *
     * @param outputs the list of output mediators to map backward.
     * @param <I> the input data type.
     * @param <O> the output data type.
     * @return a backward mapping of the given output mediators.
     * @throws AssertionError if any two output mediators share the same input mediator.
     */
    @SuppressWarnings("unchecked")
    public static <I,O> Map<Mediator<O>, Mediator<I>> create1to1BackwardMapping(List<Mediator<O>> outputs) {
        Map<Mediator<O>, Mediator<I>> map  = new HashMap<>();

        for (Mediator<O> output : outputs) {
            Mediator<I> commonAncestor = map.put(output, (Mediator<I>) output.getPrevious());
            if (commonAncestor != null)
                throw new AssertionError("Two mediators shared the same ancestor");
        }

        return map;
    }

    /**
     * Creates a mapping going from the given list of output mediators backward, but reversed (so that the mapping is
     * from an input mediator to all the output mediators which came from it).
     *
     * This is the version of 'create1to1BackwardMapping' for when there might be a one-to-many relationship
     * between input mediators and output mediators.
     *
     * @param outputs the list of output mediators to map backward.
     * @param <I> the input data type.
     * @param <O> the output data type.
     * @return mapping from input mediators to the output mediators they created (found in the given 'outputs' list).
     */
    @SuppressWarnings("unchecked")
    public static <I, O> Map<Mediator<I>, List<Mediator<O>>> createReversedBackwardMapping(List<Mediator<O>> outputs) {
        Map<Mediator<I>, List<Mediator<O>>> map = new HashMap<>();

        for (Mediator<O> output : outputs) {
            Mediator<I> input = (Mediator<I>) output.getPrevious();

            List<Mediator<O>> outputsFromInput = map.get(input);
            if (outputsFromInput == null) {
                outputsFromInput = new LinkedList<>();
                map.put(input, outputsFromInput);
            }

            outputsFromInput.add(output);
        }

        return map;
    }

    /**
     * Map the given list of output mediators back to their input mediators using the given backward mapping,
     * and add those inputs to the given 'inputs' collection.
     *
     * Output mediators not found in the mapping are discarded.
     *
     * @param outputs the list of output mediators to map backward if they are in the given mapping.
     * @param backwardMapping the mapping of output mediators to input mediators.
     * @param inputs the collection which mapped inputs are added to.
     */
    public static void mapMediatorsBackward(
            Collection<Mediator<?>> outputs,
            Map<Mediator<?>, Mediator<?>> backwardMapping,
            Collection<Mediator<?>> inputs) {

        for (Mediator<?> output : outputs) {
            Mediator<?> input = backwardMapping.get(output);

            if (input != null)
                inputs.add(input);
        }
    }
}

package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;

import java.util.*;

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
public abstract class Mediator<T> {

    /**
     * Creates an empty {@code Mediator}, which is used as the starting element from which future
     * {@code Mediator} objects (which actually contain data) are created.  Its 'data' and
     * 'previous' variables are null, and its {@link History} object is set at the epoch (ie.
     * {@code History.getEpoch()}).
     *
     * @return an empty {@code Mediator}, from which {@code Mediator} objects (which contain data)
     *         can be created.
     */
    public static Mediator<?> createEmpty() {
        return MediatorImpl.createEmpty();
    }

    /**
     * Create the output {@code Mediator} from this input {@code Mediator}, given the newly-created
     * 'data'.
     *
     * @param creator the {@link Processor} that used this input {@code Mediator} to produce the
     *                supplied data.
     * @param data the data the {@code Processor} created from this input {@code Mediator}.
     * @param <U> the type of the supplied data.
     * @return an output {@code Mediator} containing 'data' created by 'creator' using this
     *         {@code Mediator} as input.
     */
    public abstract <U> Mediator<U> createNext(Processor<?,?> creator, U data);

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
    public abstract T getData();

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
    public abstract Mediator<?> getPrevious();

    /**
     * Creates a backward mapping (ie. linking each {@code Mediator m} against
     * {@code m.getPrevious()}) where each output {@code Mediator} came from a unique input
     * {@code Mediator}.  If any two output {@code Mediator} objects came from the same input
     * {@code Mediator}, this will throw an {@code AssertionError}.
     *
     * @param outputs the list of output {@code Mediator} objects to map backward.
     * @param <I> the input data type.
     * @param <O> the output data type.
     * @return a backward mapping of the given output {@code Mediator} objects.
     * @throws AssertionError if any two output {@code Mediator} objects share the same input
     *                        mediator.
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
     * Creates a mapping going from the given list of output {@code Mediator} objects backward,
     * but reversed (so that the mapping is from an input {@code Mediator} to all the output
     * {@code Mediator} objects which came from it).
     * <p/>
     * This is the version of {@code create1to1BackwardMapping(..)} for when there might be a
     * one-to-many relationship between input and output {@code Mediator} objects.  From the
     * returned map, for each input {@code Mediator} an output {@code Mediator} can be chosen.
     * A mapping can then be made from the output to the input {@code Mediator} objects,
     * which can be used in {@link Processor}{@code .createBackwardMappingForTrainingBatch(..)}.
     *
     * @param outputs the list of output {@code Mediator} objects to map backward.
     * @param <I> the input data type.
     * @param <O> the output data type.
     * @return mapping from input to the output {@code Mediator} objects they created (found in
     *                 the given 'outputs' list).
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
     * Map the given list of output {@code Mediator} objects back to their input {@code Mediator}
     * objects using the given backward mapping, and add those inputs to the given 'inputs'
     * collection.
     * <p/>
     * Output {@code Mediator} objects not found in the mapping are discarded.
     *
     * @param outputs the list of output {@code Mediator} objects to map backward if they are in
     *                the given mapping.
     * @param backwardMapping the mapping of output to input {@code Mediator} objects.
     * @param inputs the {@link Collection} which mapped inputs are added to.
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

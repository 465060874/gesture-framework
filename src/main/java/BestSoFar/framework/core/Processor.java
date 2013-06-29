package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.History;
import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.immutables.common.ReplaceOnMutate;
import BestSoFar.framework.core.helper.TypeData;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The top-level interface for all parts of the workflow framework.
 * <p/>
 * A {@code Processor} can process input data (inside a {@link Mediator}) and return output data
 * inside another {@code Mediator}.
 * <p/>
 * Before being asked to process a {@code Mediator}, it will first process a training batch.  A
 * typical sequence that the {@code Processor} methods are called is:
 *   <p/>
 *   - {@code notify(List<Mediator<O>>)} : from observed Processors as they process the
 *       training batch.  Features of the supplied data can be gathered here.
 *   <p/>
 *   - {@code processTrainingBatch(..)} : when it's this object's turn to process the
 *     training batch.
 *   <p/>
 *   - {@code createBackwardMappingForTrainingBatch(..)} : when the completed training batch
 *     is travelling backward over the workflow that created it. This is where the
 *     {@code Processor} may learn from the training batch (possibly correlated against
 *     previously-gathered data features).
 * <p/>
 * This sequence allows allows for the {@code Processor} to prepare itself,
 * for example it might choose to process the data in a certain way that worked well during
 * training.
 * <p/>
 * It can also be observed by {@link BestSoFar.framework.core.common.ProcessObserver} objects, which are notified when input data
 * (either individually or in a batch) has been processed (but before the output is returned).
 */
public interface Processor<I,O> extends ReplaceOnMutate<Processor<I, O>> {

    /**
     * Returns true iff the {@code process(input)} method can run.
     *
     * @return true iff the {@code process(input)} method can run.
     */
    boolean isValid();

    /**
     * Process the input {@link Mediator} object and return the result in an output {@code
     * Mediator} object.
     *
     * @param input input data in a {@code Mediator} object.
     * @return result of processing the input data, inside a {@code Mediator<O>} object.
     * @throws ClassCastException if input cannot be cast to {@code Mediator<I>}.
     */
    Mediator<O> process(Mediator<?> input);

    /**
     * Given a list of input {@link Mediator} objects, produce all output {@code Mediator}
     * objects that this {@link Processor} could possibly produce.  The input data will be for
     * training, and if this {@code Processor} expects to be notified of prior {@code Processors'}
     * completions, it will be notified by the {@code notify(List<Mediator<?>>)} method,
     * which contains all training data.
     * <p/>
     * For elemental {@code Processors}, this means applying {@code process(input)} on each
     * input, and for {@code Processors} comprising multiple {@link Workflow} objects this means
     * processing each input using all appropriate {@code Workflows}.
     *
     * @param inputs list of {@code Mediator} objects containing input data.
     * @return all output {@code Mediators} that this could possibly produce.
     * @throws ClassCastException if any input cannot be cast to {@code Mediator<I>}.
     */
    List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs);

    /**
     * After the training batch has been processed to completion, this method is called to map
     * this {@link Processor} object's output {@link Mediator} objects with their input
     * {@code Mediators}.
     * <p/>
     * Elemental {@code Processors} will need only to map each {@code Mediator m} with
     * {@code m.getPrevious()} (see {@code Mediator.createBackwardMappingFor1to1(..)}).
     * <p/>
     * {@code Processors} containing multiple ways of processing the same input will have
     * created multiple outputs per input, in which case this method must choose which output
     * would have been created had the input been given to {@code process(Mediator)}.  Other
     * outputs should not appear in the returned map, and to ensure this the returned map's
     * values collection will be checked for duplicates.
     * <p/>
     * If this {@code Processor} needs training or optimising, this is the method to do it.
     * <p/>
     * If this {@code Processor} intends to train a classifier to be used in {Code process(input)}
     * to select the best strategy for each individual input data, it is advisable to create a
     * classifier for each input {@code Mediator's} {@link History} object.  This means that data
     * that is created differently is classified differently.
     * <p/>
     * One approach would be to use a {@code Map<History, Classifier>} object that is queried in
     * the {@code process(input)} method to get the appropriate
     * {@link BestSoFar.framework.concretions.helper.Classifier} for the given
     * {@code input.getHistory()} object.
     * <p/>
     * It is left to the concrete class to decide whether to do this or not (eg. the training set
     * would be larger by not doing this).
     *
     * @param completedOutputs the Mediator objects this object returned from the last call
     *                         to 'processTrainingBatch'.
     * @param successfulOutputs the Mediator objects in 'completedOutputs' which went on to
     *                          be successful.
     * @return
     */
    Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(
            List<Mediator<?>> completedOutputs,
            Set<Mediator<?>> successfulOutputs
    );

    /**
     * Gets the input/output {@link TypeData} required by this {@link Processor}.
     *
     * @return the input/output types required by this {@code Processor}.
     */
    TypeData<I, O> getTypeData();

    /**
     * The most-concrete subclass must implement this method, and return: {@code return new
     * ProcessorSubClass(this, dataType);}
     * <p/>
     * Eventually this method will be removed and proper dependency injection will be used instead.
     *
     * @return a copy of this {@code Processor}, with the given {@code TypeData}.
     */
    <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData);

    /**
     * Replaces this object with the provided clone.
     * <p/>
     * For example, calling this method on a workflow will instruct its parent to replace it in
     * its list of workflows with the clone, as well as instructing the workflow's children that
     * they have a new parent.
     * <p/>
     * This can be called in response to this object being mutated.
     *
     * @param clone the clone to replace this with.
     * @param <I2> the input type of the clone.
     * @param <O2> the output type of the clone.
     */
    <I2, O2> void replaceSelfWithClone(Processor<I2, O2> clone);
}

package BestSoFar.framework.core;

import BestSoFar.framework.helper.*;
import BestSoFar.framework.helper.Observable;
import BestSoFar.immutables.TypeData;

import java.util.*;

/**
 * The top-level interface for all workflow elements.
 *
 * A Processor can process individual inputs, or a training batch.  When a training
 * batch completes, the processor is notified (allowing for the processor to train
 * itself).
 *
 * It can also be observed by ProcessorObserver objects, which are notified when
 * input data (either individually or in a batch) has been processed (but before the
 * output is returned).
 */
public interface Processor<I,O> extends Observable<ProcessorObserver<O>>, ProcessorObserverNotifier<O> {

    /**
     * Returns true iff the 'process' method can run.
     *
     * @return true iff the 'process' method can run.
     */
    boolean isValid();

    /**
     * Process the input mediator object and return the result in an
     * output mediator object.
     *
     * @param input input data in a mediator object.
     * @return result of processing the input data, inside a mediator object.
     * @throws ClassCastException if input cannot be cast to Mediator<I>.
     */
    Mediator<O> process(Mediator<?> input);

    /**
     * Given a list of input mediator objects, produce all output mediator
     * objects that this Processor could possibly produce.  The input data
     * will be for training, and if this Processor expects to be notified
     * of prior processeses' completions, it will be notified by the
     * 'notify(List<Mediator<?>>)' method, which contains all training data.
     *
     * For elemental Processors, this means applying 'process(input)' on
     * each input.  Processors comprising multiple workflows must process
     * each input using all appropriate workflows.
     *
     * @param inputs list of mediator objects containing input data.
     * @return all output mediators that this could possibly produce.
     * @throws ClassCastException if any input cannot be cast to Mediator<I>.
     */
    List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs);

    /**
     * After the training batch has been processed to completion, this method is
     * called to map this Processor's output mediators with their input mediators.
     *
     * Elemental Processors will need only to map each mediator 'm' with 'm.getPrevious()'.
     *
     * Processors containing multiple ways of processing the same input will have
     * created multiple outputs per input, in which case this method must choose
     * which output would have been created had the input been given to
     * 'process(Mediator)'.  Other outputs should not appear in the returned map,
     * and to ensure this the returned map's values collection will be checked
     * elsewhere for duplicates.
     *
     * If this Processor needs training, this is the method to do it.  Before the
     * 'processTrainingBatch(List<Mediator<I>>)' method ran, this object will have
     * been notified of its observed processor's batch output (from which this object
     * can gather features) and the supplied 'successfulOutputs' set gives a
     * classification to train on.
     *
     * It is advisable to create a classifier for each input mediator's history object,
     * eg. have a 'Map<History, Classifier> map' object that is queried in the
     * 'process(input)' method to get the appropriate classifer -
     * 'map.get(input.getHistory())'.  This means that data which has been processed
     * differently is classified differently.  It is left to the concrete class to decide
     * whether to do this or not (eg. the training set would be larger by not doing this).
     *
     * NB. Both parameters will be mutated after this function returns, so if this object
     * needs them it must make a defensive copy.
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
     * Gets the input/output types required by this processor.
     *
     * @return the input/output types required by this processor.
     */
    TypeData<I, O> getTypeData();

    /**
     * The most-concrete subclass must implement this method, and return:
     * return new ProcessorSubClass(this, dataType);
     *
     * @return a copy of this.
     */
    <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData);
}

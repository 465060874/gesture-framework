package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.*;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.History;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.mock.TopProcessor;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The top-level interface for all parts of the workflow framework.
 * <p/>
 * A {@code Processor} can process input data (inside a {@link Mediator}) and return output data
 * inside another {@code Mediator}.
 * <p/>
 * Before being asked to process a {@code Mediator}, it will first process a training batch.  A
 * typical sequence that the {@code Processor} methods are called in is:
 *   <p/>
 *   - {@code notify(List<Mediator<O>>)} : from observed Processors as they process the
 *       training batch.  Features of the supplied data can be gathered here. NB. this is only
 *       called if the {@code Processor} is a {@link ElementObserver}.
 *   <p/>
 *   - {@code processTrainingBatch(..)} : when it's this object's turn to process the
 *     training batch.
 *   <p/>
 *   - {@code processCompletedTrainingBatch(..)} : when the completed training batch
 *     is travelling backward over the workflow that created it. This is where the
 *     {@code Processor} may learn from the training batch (possibly correlated against
 *     previously-gathered data features).
 * <p/>
 * This sequence allows allows for the {@code Processor} to prepare itself,
 * for example it might choose to process the data in a certain way that worked well during
 * training.
 */
public interface Processor
        extends EventuallyImmutable, XMLSerialisable, Versioned,
                Replaceable, Controllable, Deletable, Validatable {


    /**
     * Process the input {@link Mediator} object and return the result in an output {@code
     * Mediator} object.
     *
     * @param input input data in a {@code Mediator} object.
     * @return result of processing the input data, inside a {@code Mediator<O>} object.
     * @throws ClassCastException if input cannot be cast to {@code Mediator<I>}.
     */
    Mediator process(Mediator input);

    /**
     * Given a {@link Mediator} object containing training data, produce all output {@code
     * Mediator} objects that this {@link Processor} could possibly produce.  The input data will
     * be for
     * training, and if this {@code Processor} expects to be notified of prior {@code Processor}
     * objects' completions, it will be notified by the {@code notify(List<Mediator<?>>)} method,
     * which contains all training data.
     * <p/>
     * For elemental {@code Processors}, this means applying {@code process(input)} on each
     * input, and for {@code Processors} comprising multiple {@link Workflow} objects this means
     * processing each input using all appropriate {@code Workflows}.
     *
     * @param input set of {@code Mediator} objects containing input data.
     * @return all output {@code Mediators} that this could possibly produce,
     *          indexed by each input mediator that created them.
     */
    List<Mediator> processTrainingData(Mediator input);

    /**
     * Gets the input/output {@link TypeData} required by this {@link Processor}.
     *
     * @return the input/output types required by this {@code Processor}.
     */
    TypeData getTypeData();

    /**
     * This method is called after a training batch has been processed to completion, and returns
     * a rolled-back version of the supplied {@link CompletedTrainingBatch}.
     * <p/>
     * The supplied {@code completedTrainingBatch} object contains the {@code Mediator} objects
     * returned from the last call to {@code processTrainingBatch(..)}, and which of those went on
     * to be successful.
     * <p/>
     * "Rolling-back" means reverting these output {@code Mediator<O>} objects to input
     * {@code Mediator<I>} objects, and correctly marking which of those went on to be successful.
     * <p/>
     * {@code Processor} objects containing multiple ways of processing the same input will have
     * created multiple outputs per input, in which case this method must choose which output
     * would have been created had the input been given to {@code process(input)}.  Only if this
     * output was successful will its corresponding input mediator will be marked as successful.
     * <p/>
     * If this {@code Processor} needs training or optimising, this is the method to do it.
     * <p/>
     * If this {@code Processor} intends to train a classifier to be used in {Code process(input)}
     * to select the best strategy for each individual input data, it is advisable to create a
     * classifier for each input {@code Mediator} object's {@link History} object.  This means
     * that data that is created differently is classified differently.
     * <p/>
     * One approach would be to use a {@code Map<History, Classifier>} object that is queried in
     * the {@code process(input)} method to get the appropriate
     * {@link io.github.samwright.framework.learner.Classifier} for the given
     * {@code input.getHistory()} object.
     * <p/>
     * It is left to the concrete class to decide whether to do this or not (eg. the training set
     * per classifier would be larger by not doing this).
     *
     * @param completedTrainingBatch the completed training batch, containing the {@link Mediator}
     *                               objects returned from the last call to
     *                               {@code processTrainingBatch(..)}, and which of those went on
     *                               to be successful.
     * @return the rolled-back {@code CompletedTrainingBatch}.
     */
    CompletedTrainingBatch processCompletedTrainingBatch(
            CompletedTrainingBatch completedTrainingBatch);

    /**
     * Gets the ultimate ancestor of this {@code Processor} if it is in a complete model (ie. the
     * ultimate ancestor is a {@link TopProcessor}.  Otherwise returns null.
     *
     * @return the ultimate ancestor of this object if it is a {@code TopProcessor}
     */
    TopProcessor getTopProcessor();

    @Override
    Processor createMutableClone();

    @Override
    Processor getCurrentVersion();

    @Override
    Processor withXML(Element node, Map<UUID, Processor> dictionary);

    @Override
    Processor getNext();

    @Override
    Processor getPrevious();

}

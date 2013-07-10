package BestSoFar.framework.core.common;

import BestSoFar.framework.core.Processor;
import BestSoFar.framework.core.helper.Mediator;

import java.util.List;

/**
 * An object the observes the processing of data in a {@link Processor}.
 */
public interface ProcessObserver<T> {
    /**
     * Notify this {@code ProcessObserver} that a {@link Processor} it observes is about to output
     * a {@link Mediator} object.
     *
     * @param mediator the {@link Mediator} the observed {@link Processor} is about to output.
     */
    void notify(Mediator<T> mediator);

    /**
     * Notify this {@code ProcessObserver} that a {@link Processor} it observes is about to output
     * a batch of {@link Mediator} objects (ie. a training batch).
     *
     * @param mediators the batch of {@link Mediator} objects the observed {@link Processor} is
     *                  about to output.
     */
    void notify(List<Mediator<T>> mediators);
}

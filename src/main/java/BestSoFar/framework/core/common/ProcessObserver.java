package BestSoFar.framework.core.common;

import BestSoFar.framework.core.helper.Mediator;

import java.util.List;

/**
 * An object the observes the processing of data in a {@link BestSoFar.framework.core.Processor}.
 */
public interface ProcessObserver<T> {
    /**
     * Notify this ProcessObserver that a Processor it observes is about to output
     * a mediator object.
     *
     * @param mediator the mediator the observed Processor is about to output.
     */
    void notify(Mediator<T> mediator);

    /**
     * Notify this ProcessObserver that a Processor it observes is about to output
     * a batch of mediator objects (ie. a training batch).
     *
     * @param mediators the batch of mediators the observed Processor is about to output.
     */
    void notify(List<Mediator<T>> mediators);
}

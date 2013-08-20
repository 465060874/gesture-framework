package io.github.samwright.framework.model.common;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;

import java.util.List;

/**
 * An object the observes the processing of data in a {@link Element}.
 */
public interface ElementObserver {

    /**
     * Notify this {@code ElementObserver} that a {@link Processor} it observes is about to output
     * a {@link Mediator} object.
     *
     * @param mediator the {@link Mediator} the observed {@link Processor} is about to output.
     */
    void notify(Mediator mediator);

    /**
     * Notify this {@code ElementObserver} that a {@link Processor} it observes is about to output
     * a batch of {@link Mediator} objects (ie. a training batch).
     *
     * @param mediators the batch of {@link Mediator} objects the observed {@link Processor} is
     *                  about to output.
     */
    void notify(List<Mediator> mediators);

}

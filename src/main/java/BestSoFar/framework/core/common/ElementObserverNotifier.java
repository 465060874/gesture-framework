package BestSoFar.framework.core.common;

import BestSoFar.framework.core.helper.Mediator;

import java.util.List;

/**
 * A class implementing this will have the ability to notify ProcessorObservers.
 */
public interface ElementObserverNotifier<T> {
    /**
     * Notify all ProcessorObservers that the supplied mediator has been created by their observed Processors.
     *
     * @param mediator the newly-created mediator.
     */
    public void notifyObservers(Mediator<T> mediator);

    /**
     * Notify all ProcessorObservers that the supplied batch of mediators has been created by their observed
     * Processors.
     *
     * @param mediators the newly-created batch of mediators.
     */
    public void notifyObservers(List<Mediator<T>> mediators);

}

package BestSoFar.framework.core.common;

import BestSoFar.framework.core.helper.Mediator;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:53
 */
public interface ObservableProcess<T> {

    /**
     * Notify all ProcessObservers that the supplied mediator has been created by their observed
     * Processors.
     *
     * @param mediator the newly-created mediator.
     */
    public void notifyObservers(Mediator<T> mediator);

    /**
     * Notify all ProcessObservers that the supplied batch of mediators has been created by their
     * observed Processors.
     *
     * @param mediators the newly-created batch of mediators.
     */
    public void notifyObservers(List<Mediator<T>> mediators);
}

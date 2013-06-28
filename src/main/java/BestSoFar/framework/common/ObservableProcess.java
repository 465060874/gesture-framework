package BestSoFar.framework.common;

import BestSoFar.framework.helper.Mediator;

import java.util.List;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:53
 */
public interface ObservableProcess<T> {
    /**
     * Register an observer.
     *
     * Attempting to register an already-registered observer should have no effect.
     *
     * @param observer the observer to register.
     */
    void addObserver(ProcessObserver<T> observer);

    /**
     * Returns the immutable set of observers that are observing this ObservableProcess.
     *
     * @return observers of this ObservableProcess.
     */
    Set<ProcessObserver<T>> getObservers();

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

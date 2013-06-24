package attempt2;

import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:53
 */
public interface Observable<T> {
    /**
     * Register an observer.
     *
     * Attempting to register an already-registered observer should have no effect.
     *
     * @param observer the observer to register.
     */
    void addObserver(T observer);

    /**
     * Deregister an observer previously registered using 'addObserver'.
     *
     * Attempting to deregister an unregistered observer should have no effect.
     *
     * @param observer the observer to deregister.
     */
    void removeObserver(T observer);

    /**
     * Returns an immutable set of observers that are observing this Observable.
     *
     * @return observers of this Observable.
     */
    Set<T> getObservers();
}

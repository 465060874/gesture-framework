package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ObservableProcess;
import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.immutables.ImmutableSet;
import BestSoFar.framework.immutables.common.MutationHandler;

import java.util.List;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 22:36
 */
public class ObserverSet<T>
        extends ImmutableSet<ProcessObserver<T>> implements ObservableProcess<T> {

    public ObserverSet(MutationHandler mutationHandler) {
        super(mutationHandler);
    }

    @DoNotAdvise
    @Override
    public void notifyObservers(Mediator<T> mediator) {
        // Getting the iterator requires read-lock, after which the iterator always points at the
        // immutable list.  As such, there's no need to advise this method.
        for (ProcessObserver<T> observer : this)
            observer.notify(mediator);
    }

    @DoNotAdvise
    @Override
    public void notifyObservers(List<Mediator<T>> mediators) {
        // Getting the iterator requires read-lock, after which the iterator always points at the
        // immutable list.  As such, there's no need to advise this method.
        for (ProcessObserver<T> observer : this)
            observer.notify(mediators);
    }

    @Override
    public ObserverSet<T> assignReplacementTo(MutationHandler mutationHandler) {
        return (ObserverSet<T>) super.assignReplacementTo(mutationHandler);
    }
}

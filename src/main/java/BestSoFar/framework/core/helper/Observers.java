package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.immutables.ImmutableSet;
import BestSoFar.framework.immutables.common.SelfReplacingImmutable;

import java.util.List;
import java.util.Set;

/**
 * User: Sam Wright Date: 02/07/2013 Time: 09:42
 */
public class Observers {

    public static <T> void notifyObservers(Set<ProcessObserver<T>> observers,
                                           Mediator<T> mediator) {
        for (ProcessObserver<T> observer : observers)
            observer.notify(mediator);
    }

    public static <T> void notifyObservers(Set<ProcessObserver<T>> observers,
                                           List<Mediator<T>> mediators) {
        for (ProcessObserver<T> observer : observers)
            observer.notify(mediators);
    }

    @SuppressWarnings("unchecked")
    public static <T> ImmutableSet<ProcessObserver<T>> updateObservers(ImmutableSet<ProcessObserver<T>> observers) {

        ImmutableSet<ProcessObserver<T>> newObservers = new ImmutableSet<>(true);
        observers.finalise();

        for (ProcessObserver<T> observer : observers) {
            if (observer instanceof SelfReplacingImmutable) {
                SelfReplacingImmutable replaceableObserver = (SelfReplacingImmutable) observer;
                observer = (ProcessObserver<T>) replaceableObserver.getLatest();
            }

            if (observer instanceof Deletable) {
                if ( !((Deletable) observer).isDeleted() )
                    newObservers.add(observer);
            } else {
                newObservers.add(observer);
            }
        }

        return newObservers;
    }

}

package BestSoFar.framework.helper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of Observable.
 */
public class ProcessorObserverManager<T> implements Observable<ProcessorObserver<T>>, ProcessorObserverNotifier<T> {
    private final Set<ProcessorObserver<T>> observers = new HashSet<>();

    @Override
    public void addObserver(ProcessorObserver<T> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ProcessorObserver<T> observer) {
        observers.remove(observer);
    }

    @Override
    public Set<ProcessorObserver<T>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }

    @Override
    public void notifyObservers(Mediator<T> mediator) {
        for (ProcessorObserver<T> observer : observers)
            observer.notify(mediator);
    }

    @Override
    public void notifyObservers(List<Mediator<T>> mediators) {
        for (ProcessorObserver<T> observer : observers)
            observer.notify(mediators);
    }
}

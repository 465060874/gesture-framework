package attempt2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of Observable.
 */
public class ObservableImpl<T> implements Observable<T> {
    private final Set<T> observers = new HashSet<>();

    @Override
    public void addObserver(T observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(T observer) {
        observers.remove(observer);
    }

    @Override
    public Set<T> getObservers() {
        return Collections.unmodifiableSet(observers);
    }
}

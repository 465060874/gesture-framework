package attempt2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of the Observable properties of Processor.
 */
public abstract class ObservableProcessor<I, O> implements Processor<I, O> {
    private final Set<ProcessorObserver<O>> observers = new HashSet<>();

    @Override
    public void addObserver(ProcessorObserver<O> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ProcessorObserver<O> observer) {
        observers.remove(observer);
    }

    @Override
    public Set<ProcessorObserver<O>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }
}

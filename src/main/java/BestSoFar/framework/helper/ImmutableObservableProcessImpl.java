package BestSoFar.framework.helper;

import BestSoFar.immutables.ReplaceOnMutate;
import com.sun.istack.internal.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ObservableProcess.
 */
public class ImmutableObservableProcessImpl<T> implements ObservableProcess<T> {
    @NotNull private final Set<ProcessObserver<T>> observers;
    @NotNull private final ReplaceOnMutate replaceOnMutate;
    @NotNull private Set<ProcessObserver<T>> observersForClone;


    public ImmutableObservableProcessImpl(ReplaceOnMutate replaceOnMutate) {
        this(Collections.<ProcessObserver<T>>emptySet(), replaceOnMutate);
    }

    public ImmutableObservableProcessImpl(Set<ProcessObserver<T>> observers,
                                          ReplaceOnMutate replaceOnMutate) {
        this.observersForClone = this.observers = Collections.unmodifiableSet(observers);
        this.replaceOnMutate = replaceOnMutate;
    }

    /**
     * Create a clone of this object, including any mutation already made.
     *
     * @param replaceOnMutate
     * @return
     */
    public ObservableProcess<T> cloneFor(ReplaceOnMutate replaceOnMutate) {
        return new ImmutableObservableProcessImpl<>(observersForClone, replaceOnMutate);
    }

    @Override
    public void addObserver(ProcessObserver<T> observer) {
        if (observersForClone != observers)
            throw new RuntimeException("Can only mutate once");

        if (observers.contains(observer))
            return;

        Set<ProcessObserver<T>> observersForClone = new HashSet<>(observers);
        observersForClone.add(observer);


        replaceOnMutate.handleMutation();
    }

    @Override
    public Set<ProcessObserver<T>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }

    @Override
    public void notifyObservers(Mediator<T> mediator) {
        for (ProcessObserver<T> observer : observers)
            observer.notify(mediator);
    }

    @Override
    public void notifyObservers(List<Mediator<T>> mediators) {
        for (ProcessObserver<T> observer : observers)
            observer.notify(mediators);
    }
}

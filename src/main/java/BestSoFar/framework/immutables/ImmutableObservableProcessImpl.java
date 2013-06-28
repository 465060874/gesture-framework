package BestSoFar.framework.immutables;

import BestSoFar.framework.common.ObservableProcess;
import BestSoFar.framework.common.ProcessObserver;
import BestSoFar.framework.helper.Mediator;
import BestSoFar.framework.immutables.common.ImmutableReplacement;
import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ObservableProcess.
 */
public class ImmutableObservableProcessImpl<T>
        implements ObservableProcess<T>, ImmutableReplacement<ImmutableObservableProcessImpl<T>> {

    @Getter @NonNull private final Set<ProcessObserver<T>> observers;
    @NonNull private final MutationHandler mutationHandler;
    @NonNull private Set<ProcessObserver<T>> mutatedObservers;
    @Getter private ImmutableObservableProcessImpl<T> replacement;


    public ImmutableObservableProcessImpl(MutationHandler mutationHandler) {
        this(Collections.<ProcessObserver<T>>emptySet(), mutationHandler);
    }

    public ImmutableObservableProcessImpl(Set<ProcessObserver<T>> observers,
                                          MutationHandler mutationHandler) {
        this.mutatedObservers = this.observers = Collections.unmodifiableSet(observers);
        this.mutationHandler = mutationHandler;
    }

    @Override
    public void addObserver(ProcessObserver<T> observer) {
        if (hasReplacement() || replacementIsMutated())
            throw new AlreadyMutatedException();

        if (observers.contains(observer))
            return;

        mutatedObservers = new HashSet<>(observers);
        mutatedObservers.add(observer);

        mutationHandler.handleMutation();
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

    @Override
    public ImmutableObservableProcessImpl<T> makeReplacementFor(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new AlreadyMutatedException();

        replacement = new ImmutableObservableProcessImpl<>(mutatedObservers, mutationHandler);
        return replacement;
    }

    @Override
    public void forgetReplacement() {
        replacement = null;
    }

    @Override
    public boolean hasReplacement() {
        return replacement != null;
    }

    @Override
    public boolean replacementIsMutated() {
        return observers != mutatedObservers;
    }
}

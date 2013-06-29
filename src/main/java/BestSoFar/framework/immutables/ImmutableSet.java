package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Delegate;

import java.util.*;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 20:16
 */
public class ImmutableSet<E> extends ImmutableWrapper<Set<E>> implements Set<E> {

    @Delegate
    private Set<E> delegate;

    public ImmutableSet(MutationHandler mutationHandler) {
        super(mutationHandler);
        delegate = Collections.emptySet();
    }

    private ImmutableSet(Set<E> mutableClone) {
        this.delegate = mutableClone;
    }

    @Override
    public ImmutableSet<E> assignReplacementTo(MutationHandler mutationHandler) {
        return (ImmutableSet<E>) super.assignReplacementTo(mutationHandler);
    }

    @Override
    ImmutableWrapper<Set<E>> createMutableClone() {
        return new ImmutableSet<>(new HashSet<>(delegate));
    }

    @Override
    void makeDelegateImmutable() {
        delegate = Collections.unmodifiableSet(delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}

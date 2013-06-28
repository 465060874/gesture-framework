package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Delegate;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 20:16
 */
public class ImmutableSet<E> extends ImmutableWrapper<Set<E>> implements Set<E> {

    @Delegate private Set<E> active;

    public ImmutableSet(MutationHandler mutationHandler) {
        this(Collections.<E>emptySet(), mutationHandler);
    }

    private ImmutableSet(Set<E> set, @NonNull MutationHandler mutationHandler) {
        super(set, mutationHandler);
    }

    @Override
    ImmutableWrapper<Set<E>> createNewFromMutated(Set<E> mutated, MutationHandler mutationHandler) {
        return new ImmutableSet<>(Collections.unmodifiableSet(mutated), mutationHandler);
    }

    @Override
    Set<E> cloneDelegateAsMutable(Set<E> delegate) {
        return new HashSet<>(delegate);
    }

    @Override
    void setActiveDelegate(Set<E> delegate) {
        active = delegate;
    }
}

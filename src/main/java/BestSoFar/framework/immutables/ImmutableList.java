package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Delegate;
import lombok.NonNull;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 13:47
 */

public class ImmutableList<E> extends ImmutableWrapper<List<E>> implements List<E> {

    @Delegate private List<E> active;

    public ImmutableList(MutationHandler mutationHandler) {
        this(Collections.<E>emptyList(), mutationHandler);
    }

    private ImmutableList(List<E> list, @NonNull MutationHandler mutationHandler) {
        super(list, mutationHandler);
    }

    @Override
    ImmutableWrapper<List<E>> createNewFromMutated(List<E> mutated, MutationHandler mutationHandler) {
        return new ImmutableList<>(Collections.unmodifiableList(mutated), mutationHandler);
    }

    @Override
    List<E> cloneDelegateAsMutable(List<E> delegate) {
        List<E> clone;
        if (ArrayList.class.isAssignableFrom(delegate.getClass()))
            clone = new ArrayList<>(delegate);
        else
            clone = new LinkedList<>(delegate);

        return clone;
    }

    @Override
    void setActiveDelegate(List<E> delegate) {
        active = delegate;
    }

    public boolean replace(E oldElement, E newElement) {
        int index = indexOf(oldElement);
        return index != -1 && set(index, newElement) == oldElement;
    }

    @Override
    public ImmutableList<E> makeReplacementFor(MutationHandler mutationHandler) {
        return (ImmutableList<E>) super.makeReplacementFor(mutationHandler);
    }
}

package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 10:10
 */
public class ImmutableList<E> extends ImmutableWrapper<List<E>> implements List<E> {

    @Delegate private List<E> delegate;

    public ImmutableList(MutationHandler mutationHandler) {
        super(mutationHandler);
        delegate = Collections.emptyList();
    }

    private ImmutableList(List<E> mutableClone) {
        this.delegate = mutableClone;
    }

    @Override
    public ImmutableList<E> assignReplacementTo(MutationHandler mutationHandler) {
        return (ImmutableList<E>) super.assignReplacementTo(mutationHandler);
    }

    @Override
    ImmutableWrapper<List<E>> createMutableClone() {
        List<E> clone;
        if (ArrayList.class.isAssignableFrom(delegate.getClass()))
            clone = new ArrayList<>(delegate);
        else
            clone = new LinkedList<>(delegate);

        return new ImmutableList<>(clone);
    }

    @Override
    void makeDelegateImmutable() {
        delegate = Collections.unmodifiableList(delegate);
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

    @DoNotAdvise
    public boolean replace(E oldElement, E newElement) {
        int index = indexOf(oldElement);
        return index != -1 && set(index, newElement) == oldElement;
    }
}

package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.HandledImmutable;
import BestSoFar.framework.immutables.common.Immutable;
import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 10:10
 */
public class ImmutableList<E> extends ImmutableWrapper implements List<E> {

    @Delegate private List<E> delegate;

    public ImmutableList(boolean mutable) {
        super(mutable);
        delegate = Collections.emptyList();
    }

    private ImmutableList(List<E> delegate, boolean mutable) {
        super(mutable);
        this.delegate = delegate;
    }

    @Override
    public ImmutableList<E> createClone(boolean mutable) {
        if (mutable) {
            List<E> clone;
            if (ArrayList.class.isAssignableFrom(delegate.getClass()))
                clone = new ArrayList<>(delegate);
            else
                clone = new LinkedList<>(delegate);

            return new ImmutableList<>(clone, true);

        } else {
            return new ImmutableList<>(delegate, false);
        }
    }

    @Override
    public void finalise() {
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

    @DoNotAdvise
    public void replaceOrAdd(E oldElement, E newElement) {
        for (int i = 0; i < size(); ++i) {
            E pointer = get(i);
            if (pointer == oldElement) {
                set(i, newElement);
                return;
            }

            if (pointer == newElement)
                return;
        }

        add(newElement);
    }
}

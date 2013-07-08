package BestSoFar.framework.immutables;

import lombok.Delegate;

import java.util.*;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 20:16
 */
public class ImmutableSet<E> extends ImmutableWrapper implements Set<E> {

    @Delegate
    private Set<E> delegate;

    public ImmutableSet(boolean mutable) {
        super(mutable);
        if (mutable)
            delegate = Collections.emptySet();
        else
            delegate = new HashSet<>();
    }

    private ImmutableSet(Set<E> delegate, boolean mutable) {
        super(mutable);
        this.delegate = delegate;
    }

    @Override
    public ImmutableSet<E> createClone(boolean mutable) {
        Set<E> newDelegate;
        if (mutable)
            newDelegate = new HashSet<>(delegate);
        else
            newDelegate = delegate;

        return new ImmutableSet<>(newDelegate, mutable);
    }

    @Override
    public void finalise() {
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

    @DoNotAdvise
    public boolean replace(E oldElement, E newElement) {
        if (contains(oldElement)) {
            if (isMutable()) {
                remove(oldElement);
                add(newElement);
            } else {
                ImmutableSet<E> clone = createClone(true);
                clone.remove(oldElement);
                clone.add(newElement);
                proposeReplacement(clone);
            }
            return true;
        } else {
            return false;
        }
    }
}

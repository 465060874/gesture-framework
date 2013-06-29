package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.NewMutationHandler;
import lombok.Delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 10:10
 */
public class NewImmutableList<E> extends NewImmutableWrapper<List<E>> implements List<E> {

    @Delegate private List<E> delegate;

    public NewImmutableList(NewMutationHandler mutationHandler) {
        super(mutationHandler);
        delegate = Collections.emptyList();
    }

    private NewImmutableList(List<E> mutableClone) {
        this.delegate = mutableClone;
    }

    @Override
    public NewImmutableList<E> assignReplacementTo(NewMutationHandler mutationHandler) {
        return (NewImmutableList<E>) super.assignReplacementTo(mutationHandler);
    }

    @Override
    NewImmutableWrapper<List<E>> createMutableClone() {
        List<E> clone;
        if (ArrayList.class.isAssignableFrom(delegate.getClass()))
            clone = new ArrayList<>(delegate);
        else
            clone = new LinkedList<>(delegate);

        return new NewImmutableList<>(clone);
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
}

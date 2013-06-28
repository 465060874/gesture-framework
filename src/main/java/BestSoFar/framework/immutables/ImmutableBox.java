package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import BestSoFar.framework.immutables.helper.Box;
import BestSoFar.framework.immutables.helper.BoxImpl;
import lombok.Delegate;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 23:01
 */
public class ImmutableBox<T> extends ImmutableWrapper<Box<T>> implements Box<T> {

    @Delegate private Box<T> active;

    public ImmutableBox(Box<T> delegate, @NonNull MutationHandler mutationHandler) {
        super(delegate, mutationHandler);
    }

    @Override
    ImmutableWrapper<Box<T>> createNewFromMutated(Box<T> mutated, MutationHandler mutationHandler) {
        return new ImmutableBox<>(mutated, mutationHandler);
    }

    @Override
    Box<T> cloneDelegateAsMutable(Box<T> delegate) {
        return new BoxImpl<>(delegate.getContents(), true);
    }

    @Override
    void setActiveDelegate(Box<T> delegate) {
        active = delegate;
    }
}
package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.MutationHandler;
import BestSoFar.framework.immutables.helper.Box;
import BestSoFar.framework.immutables.helper.BoxImpl;
import lombok.Delegate;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 23:01
 */
public class ImmutableBox<T> extends ImmutableWrapper<Box<T>> implements Box<T> {

    @Delegate private Box<T> box;

    public ImmutableBox(T contents, MutationHandler mutationHandler) {
        super(mutationHandler);
        box = new BoxImpl<>(contents);
        box.lock();
    }

    private ImmutableBox(T contents) {
        box = new BoxImpl<>(contents);
    }

    @Override
    ImmutableWrapper<Box<T>> createMutableClone() {
        return new ImmutableBox<>(box.getContents());
    }

    @Override
    void makeDelegateImmutable() {
        box.lock();
    }
}
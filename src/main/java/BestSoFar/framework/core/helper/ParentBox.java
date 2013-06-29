package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.immutables.ImmutableBox;
import BestSoFar.framework.immutables.common.MutationHandler;

/**
 * User: Sam Wright Date: 30/06/2013 Time: 00:34
 */
public class ParentBox<T> extends ImmutableBox<T> implements ChildOf<T> {
    public ParentBox(T contents, MutationHandler mutationHandler) {
        super(contents, mutationHandler);
    }

    @DoNotAdvise
    @Override
    public T getParent() {
        return getContents();
    }

    @DoNotAdvise
    @Override
    public void setParent(T parent) {
        setContents(parent);
    }

    @Override
    public ParentBox<T> assignReplacementTo(MutationHandler mutationHandler) {
        return (ParentBox<T>) super.assignReplacementTo(mutationHandler);
    }
}

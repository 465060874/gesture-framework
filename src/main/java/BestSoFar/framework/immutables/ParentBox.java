package BestSoFar.framework.immutables;

import BestSoFar.framework.common.ChildOf;
import BestSoFar.framework.immutables.common.MutationHandler;
import BestSoFar.framework.immutables.helper.Box;

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

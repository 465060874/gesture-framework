package BestSoFar.framework.helper;

import BestSoFar.immutables.ReplaceOnMutate;
import com.sun.istack.internal.NotNull;
import lombok.Getter;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 18:48
 */
public class ParentMutationHandler<T> implements ChildOf<T> {
    @Getter @NotNull private final T parent;
    @Getter @NotNull private T parentForClone;
    @NotNull private final ReplaceOnMutate replaceOnMutate;

    public ParentMutationHandler(T parent, ReplaceOnMutate replaceOnMutate) {
        this.replaceOnMutate = replaceOnMutate;
        this.parentForClone = this.parent = parent;
    }

    public ParentMutationHandler<T> cloneFor(ReplaceOnMutate replaceOnMutate) {
        return new ParentMutationHandler<T>(parentForClone, replaceOnMutate);
    }

    @Override
    public void setParent(T parent) {
        if(this.parent != parentForClone)
            throw new RuntimeException("Cannot mutate twice");

        parentForClone = parent;

    }

}

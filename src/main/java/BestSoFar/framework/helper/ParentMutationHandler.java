package BestSoFar.framework.helper;

import BestSoFar.immutables.MutationHandler;
import lombok.Getter;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 18:48
 */
public class ParentMutationHandler<T>
        implements ChildOf<T>, ImmutableReplacement<ParentMutationHandler<T>> {

    @Getter private ParentMutationHandler<T> replacement;
    @Getter private final T parent;
    private T parentForReplacement;
    private final MutationHandler mutationHandler;

    public ParentMutationHandler(T parent, MutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        this.parentForReplacement = this.parent = parent;
    }

    @Override
    public void setParent(T parent) {
        if (hasReplacement() || replacementIsMutated())
            throw new AlreadyMutatedException();

        parentForReplacement = parent;
    }

    @Override
    public ParentMutationHandler<T> makeReplacementFor(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new AlreadyMutatedException();

        return new ParentMutationHandler<>(parentForReplacement, mutationHandler);
    }

    @Override
    public void forgetReplacement() {
        replacement = null;
        parentForReplacement = parent;
    }

    @Override
    public boolean hasReplacement() {
        return replacement != null;
    }

    @Override
    public boolean replacementIsMutated() {
        return parent != parentForReplacement;
    }
}

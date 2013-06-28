package BestSoFar.framework.helper;

import BestSoFar.immutables.MutationHandler;
import BestSoFar.immutables.ReplaceOnMutate;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 10:47
 */
public interface ImmutableReplacement<T> {
    class AlreadyMutatedException extends RuntimeException {}

    T makeReplacementFor(MutationHandler mutationHandler);
    void forgetReplacement();
    boolean hasReplacement();
    boolean replacementIsMutated();
    T getReplacement();
}

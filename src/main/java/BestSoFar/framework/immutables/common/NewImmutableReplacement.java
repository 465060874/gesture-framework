package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 21:09
 */
public interface NewImmutableReplacement {
    class AlreadyMutatedException extends RuntimeException {}

    class ReplacementNotHandled extends RuntimeException {
    }

    NewImmutableReplacement assignReplacementTo(NewMutationHandler mutationHandler);

    void discardReplacement();

    boolean hasReplacement();

    boolean isMutated();

    NewImmutableReplacement getReplacement();
}

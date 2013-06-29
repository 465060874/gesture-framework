package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 21:09
 */
public interface ImmutableReplacement {
    class AlreadyMutatedException extends RuntimeException {}

    class ReplacementNotHandled extends RuntimeException {
    }

    ImmutableReplacement assignReplacementTo(MutationHandler mutationHandler);

    void discardReplacement();

    boolean hasReplacement();

    boolean isMutated();

    ImmutableReplacement getReplacement();
}

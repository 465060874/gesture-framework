package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 21:09
 */
public interface HandledImmutable extends EventuallyImmutable {

    /**
     * Finalise the object, and direct future replacements to the given {@link ReplacementHandler}.
     *
     * @param replacementHandler the ReplacementHandler to pass future replacements to.
     */
    void assignToHandler(ReplacementHandler replacementHandler);

}

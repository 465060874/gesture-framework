package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 21:18
 */
public interface ReplacementHandler {
    void handleReplacement(EventuallyImmutable existingObject, EventuallyImmutable proposedObject);
}

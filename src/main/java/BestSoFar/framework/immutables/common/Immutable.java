package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 04/07/2013 Time: 10:23
 */
public interface Immutable {
    Immutable createClone(boolean mutable);

    void finalise();

    boolean isMutable();

    void proposeReplacement(Immutable proposed);
}

package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 30/06/2013 Time: 18:38
 */
public interface SelfReplacingImmutable extends Immutable {
    SelfReplacingImmutable getReplacement();

    SelfReplacingImmutable getReplaced();

    boolean hasReplacement();

    boolean hasReplaced();
    void discardReplacement();

    void discardReplaced();
    SelfReplacingImmutable getLatest();

    SelfReplacingImmutable getEarliest();
    int getAge();

}

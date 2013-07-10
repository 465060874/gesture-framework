package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.EventuallyImmutable;
import lombok.Delegate;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 13:00
 */
public class MockEventuallyImmutable implements EventuallyImmutable {


    @Override
    public EventuallyImmutable createMutableClone() {
        return null; // Dummy implementation
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        // Dummy implementation
    }

    @Override
    public boolean isMutable() {
        return false; // Dummy implementation
    }

    @Override
    public void replaceWith(EventuallyImmutable replacement) {
        // Dummy implementation
    }

    @Override
    public void discardReplacement() {
        // Dummy implementation
    }

    @Override
    public void discardOlderVersions() {
        // Dummy implementation
    }

    @Override
    public VersionInfo versionInfo() {
        return null; // Dummy implementation
    }

    @Override
    public void delete() {
        // Dummy implementation
    }

    @Override
    public boolean isDeleted() {
        return false; // Dummy implementation
    }
}

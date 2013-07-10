package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.EventuallyImmutable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Implementation of VersionInfo - do not use this class.  Use VersionInfo instead.
 */
@AllArgsConstructor
public class VersionInfoImpl extends VersionInfo {

    @Getter @NonNull final private EventuallyImmutable thisVersion;
    @Getter private int versionNumber;
    @Getter private EventuallyImmutable next, previous;


    @Override
    public VersionInfo withNext(EventuallyImmutable next) {
        return new VersionInfoImpl(thisVersion, versionNumber, previous, next);
    }

    @Override
    public VersionInfo withPrevious(EventuallyImmutable previous) {
        int age;

        if (previous != null)
            age = previous.getVersionInfo().getVersionNumber() + 1;
        else
            age = this.versionNumber;

        return new VersionInfoImpl(thisVersion, age, previous, next);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventuallyImmutable getLatest() {
        EventuallyImmutable next, pointer = thisVersion;
        while (null != (next = pointer.getVersionInfo().getNext()))
            pointer = next;

        return pointer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventuallyImmutable getEarliest() {
        EventuallyImmutable previous, pointer = thisVersion;
        while (null != (previous = pointer.getVersionInfo().getPrevious()))
            pointer = previous;

        return pointer;
    }

}

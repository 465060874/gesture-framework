package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.EventuallyImmutable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Implementation of {@link VersionInfo} - do not use this class.  Use {@code VersionInfo} instead.
 */
@AllArgsConstructor
public class VersionInfoImpl extends VersionInfo {

    @Getter @NonNull final private EventuallyImmutable thisVersion;
    @Getter private final EventuallyImmutable previous, next;


    @Override
    public VersionInfo withNext(EventuallyImmutable next) {
        return new VersionInfoImpl(thisVersion, previous, next);
    }

    @Override
    public VersionInfo withPrevious(EventuallyImmutable previous) {
        return new VersionInfoImpl(thisVersion, previous, next);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventuallyImmutable getLatest() {
        EventuallyImmutable next, pointer = thisVersion;
        while (null != (next = pointer.versionInfo().getNext()))
            pointer = next;

        return pointer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventuallyImmutable getEarliest() {
        EventuallyImmutable previous, pointer = thisVersion;
        while (null != (previous = pointer.versionInfo().getPrevious()))
            pointer = previous;

        return pointer;
    }

}

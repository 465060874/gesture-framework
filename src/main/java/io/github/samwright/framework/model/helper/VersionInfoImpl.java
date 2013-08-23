package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.common.EventuallyImmutable;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;

/**
 * Implementation of {@link VersionInfo} - do not use this class.  Use {@code VersionInfo} instead.
 */
public class VersionInfoImpl<T extends EventuallyImmutable> extends VersionInfo<T> {

    @Getter @NonNull private final T thisVersion;
    @Getter private final T previous, next;

    public VersionInfoImpl(T thisVersion, T previous, T next) {
        this.thisVersion = thisVersion;
        this.previous = previous;
        this.next = next;
    }

    @Override
    public VersionInfo<T> withNext(T next) {
        return new VersionInfoImpl<>(thisVersion, previous, next);
    }

    @Override
    public VersionInfo<T> withPrevious(T previous) {
        return new VersionInfoImpl<>(thisVersion, previous, next);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getLatest() {
        T next, latest = thisVersion;
        while (null != (next = (T) latest.versionInfo().getNext()))
            latest = next;

        return latest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getEarliest() {
        T previous, earliest = thisVersion;
        while (null != (previous = (T) earliest.versionInfo().getPrevious()))
            earliest = previous;

        return earliest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        LinkedList<T> previousVersions = new LinkedList<>();

        T pointer = thisVersion;
        while (null != (pointer = (T) pointer.versionInfo().getPrevious()))
            previousVersions.addFirst(pointer);


        LinkedList<T> nextVersions = new LinkedList<>();

        pointer = thisVersion;
        while (null != (pointer = (T) pointer.versionInfo().getNext()))
            nextVersions.addFirst(pointer);

        return String.format("%s -> %s -> %s", previousVersions, thisVersion, nextVersions);

    }
}

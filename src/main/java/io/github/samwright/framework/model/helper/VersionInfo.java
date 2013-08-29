package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.common.EventuallyImmutable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Each {@link EventuallyImmutable} object has one {@code VersionInfo} object which describes
 * tracks its earlier and later versions.
 */
public abstract class VersionInfo<T extends EventuallyImmutable> {

    /**
     * Gets the next version of the {@link EventuallyImmutable} object.  If this describes the
     * latest version, the method returns null.
     *
     * @return the next version of the {@link EventuallyImmutable} object.
     */
    public abstract T getNext();

    /**
     * Gets the previous version of the {@link EventuallyImmutable} object.  If this describes the
     * earliest (ie. first) version, the method returns null.
     *
     * @return the previous version of the {@link EventuallyImmutable} object.
     */
    public abstract T getPrevious();

    /**
     * Gets the version of the {@link EventuallyImmutable} object that this describes.
     *
     * @return the version of the {@link EventuallyImmutable} object that this describes.
     */
    public abstract T getThisVersion();

    /**
     * Returns a clone of this, but with the given {@link EventuallyImmutable} as the next version.
     *
     * @param next the {@code EventuallyImmutable} object to be the next version in the returned
     *             clone.
     * @return a clone of this, but with the given {@code EventuallyImmutable} object as the next
     *             version.
     */
    public abstract VersionInfo<T> withNext(T next);

    /**
     * Returns a clone of this, but with the given {@link EventuallyImmutable} as the previous
     * version.
     *
     * @param previous the {@code EventuallyImmutable} object to be the previous version in the
     *                 returned clone.
     * @return a clone of this, but with the given {@code EventuallyImmutable} object as the
     *                 previous version.
     */
    public abstract VersionInfo<T> withPrevious(T previous);

    /**
     * Gets the latest version of the managed {@link EventuallyImmutable} object.
     *
     * @return the latest version of the managed {@link EventuallyImmutable} object.
     */
    public abstract T getLatest();

    /**
     * Gets the earliest version of the managed {@link EventuallyImmutable} object.
     *
     * @return the earliest version of the managed {@link EventuallyImmutable} object.
     */
    public abstract T getEarliest();

    /**
     * Creates a {@code VersionInfo} object for the first version of an {@link EventuallyImmutable}
     * object (ie. without any previous or next versions, and with an age of zero).
     *
     * @param firstVersion the first version of an {@code EventuallyImmutable} object.
     * @return the version information for the given object.
     */
    public static <T2 extends EventuallyImmutable> VersionInfo<T2> createForFirst (T2 firstVersion) {
        return new VersionInfoImpl<>(firstVersion, null, null);
    }

    /**
     * Convert all {@link EventuallyImmutable} objects in the given collection to their latest
     * versions.  Other objects are not affected (i.e. they remain in the collection).
     *
     * @param oldVersions the collection of objects to update to their latest versions,
     *                    if they are {@link EventuallyImmutable} objects.
     * @param <E> the type of the given collection's elements.
     */
    @SuppressWarnings("unchecked")
    public static <E> void updateAllToLatest(Collection<E> oldVersions) {
        List<E> newVersions = new LinkedList<>();

        for (E oldVersion : oldVersions) {
            if (oldVersion instanceof EventuallyImmutable) {
                EventuallyImmutable newVersion = (EventuallyImmutable) oldVersion;
                newVersion = newVersion.versionInfo().getLatest();
                newVersions.add((E) newVersion);
            } else {
                newVersions.add(oldVersion);
            }
        }

        oldVersions.clear();
        oldVersions.addAll(newVersions);
    }
}

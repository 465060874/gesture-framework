package io.github.samwright.framework.model.common;

import java.util.UUID;

/**
 * Each {@link EventuallyImmutable} object has one {@code VersionInfo} object which describes
 * tracks its earlier and later versions.
 */
public interface Versioned {

    /**
     * Gets the next version of the {@link EventuallyImmutable} object.  If this describes the
     * latest version, the method returns null.
     *
     * @return the next version of the {@link EventuallyImmutable} object.
     */
    Versioned getNext();

    /**
     * Gets the previous version of the {@link EventuallyImmutable} object.  If this describes the
     * earliest (ie. first) version, the method returns null.
     *
     * @return the previous version of the {@link EventuallyImmutable} object.
     */
    Versioned getPrevious();

    /**
     * Discards any replacement to this object and ensures this object is not deleted, therefore
     * allowing for it to be replaced by another {@code Replaceable} object. In doing so, it makes
     * the next version discard its previous version.
     */
    void discardNext();

    /**
     * Discard older versions of this, so they may be freed by the garbage collector.  In doing so
     * it makes the previous version discard its next version.
     */
    void discardPrevious();

    /**
     * Sets the universally unique identifier for this object (and all future versions of this).
     * <p/>
     * No checks for uniqueness are performed.
     * <p/>
     *
     * @param uuid the universally unique identifier to set for this object.
     */
    void setUUID(UUID uuid);

    /**
     * Gets the universally unique identifier for this object.
     */
    UUID getUUID();

    /**
     * Sets this object as the current version (along with objects associated with it), informing
     * all relevant controllers and registers to the change.
     */
    void setAsCurrentVersion();

    /**
     * Returns the current version of this object.
     *
     * @return the current version of this object.
     */
    Versioned getCurrentVersion();
}

package io.github.samwright.framework.model.common;

import java.util.UUID;

/**
 * User: Sam Wright Date: 27/08/2013 Time: 16:57
 */
public interface HasUUID {
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

    String getModelIdentifier();
}

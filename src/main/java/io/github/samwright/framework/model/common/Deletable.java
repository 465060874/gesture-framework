package io.github.samwright.framework.model.common;

/**
 * A deletable object.
 */
public interface Deletable {
    /**
     * Delete this object.  For immutable objects in some kind of framework,
     * this ensures the object will not be in subsequent versions of the framework.
     */
    void delete();

    /**
     * Returns true iff this object was deleted.
     *
     * @return true iff this object was deleted.
     */
    boolean isDeleted();
}

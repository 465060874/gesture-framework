package io.github.samwright.framework.model.common;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.helper.VersionInfo;

/**
 * An object that can be replaced by another (or deleted), whilst keeping track of earlier and
 * later versions.
 */
public interface Replaceable {
    /**
     * Propose a replacement for this object.  How this is handled is determined by the concrete
     * class implementing this.
     * <p/>
     * This must have no next version, and the replacement must have no previous version -
     * otherwise a RuntimeException is thrown.
     * <p/>
     * As an example, a sequence of versions between {@code Replaceable}
     * objects {@code start} and {@code end} can be contracted using
     *
     * <pre>{@code
     * start.discardNext();  // start.getNext() == null
     * end.discardPrevious(); // end.getPrevious() == null
     * start.replaceWith(end);
     * }</pre>
     *
     * @param replacement the replacement to propose.
     * @throws RuntimeException if this object is still mutable,
     *                          or has already been replaced or deleted.
     */
    void replaceWith(Replaceable replacement);

    /**
     * Discards any replacement to this object and ensures this object is not deleted,
     * therefore allowing for it to be replaced by another {@code Replaceable} object.
     * In doing so, it makes the next version discard its previous version.
     */
    void discardNext();

    /**
     * Discard older versions of this, so they may be freed by the garbage collector.  In doing
     * so it makes the previous version discard its next version.
     */
    void discardPrevious();

    /**
     * Gets the {@link VersionInfo} object that describes this object.  It contains information
     * about this version of this object, and its earlier and later versions.
     *
     * @return the version information about this object.
     */
    VersionInfo versionInfo();

    /**
     * Delete this object - meaning it is no longer in use and can not be replaced.
     */
    void delete();

    /**
     * Returns true iff this object was deleted.
     *
     * @return true iff this object was deleted.
     */
    boolean isDeleted();

    void setController(ModelController modelController);

    ModelController getController();

    void undo();

    void redo();
}

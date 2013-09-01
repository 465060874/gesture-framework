package io.github.samwright.framework.model.common;

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
     * Propose that this object replace the supplied object.
     *
     * @param toReplace the object to replace.
     */
    void replace(Replaceable toReplace);

    /**
     * This is called after all relevant objects have been fixed, and presents the implementing
     * class with the opportunity to perform last-minute changes to itself now that all related
     * objects have also been fixed.
     */
    void afterReplacement();

}

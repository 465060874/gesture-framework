package BestSoFar.framework.core.common;

import BestSoFar.framework.core.helper.VersionInfo;
import BestSoFar.framework.core.helper.VersionInfo;

/**
 * An object which eventually becomes immutable.
 * <p/>
 * It might be immutable from creation, or it might start as mutable and become immutable when
 * {@code fixAsVersion(..)} is called.
 * <p/>
 * The convention to use is {@code X getX()} for accessors and
 * {@code EventuallyImmutable withX(newX)} for mutators.
 * <p/>
 * Whilst {@code this.isMutable()}, mutators mutate the internal data and return the same object.
 * Once immutable, mutators create a mutable clone and mutations are applied to it, i.e.
 * {@code createMutableClone().withX(newX)}.
 * <p/>
 * Mutations can therefore be strung together, e.g. {@code obj.withX(newX).withY(newY).withZ(newZ)}
 * will return {@code obj} with the mutations if {@code obj.isMutable()} or
 * {@code obj.createMutableClone()} with the mutations otherwise.
 * <p/>
 * Immutable objects are made aware of replacements ready to take their place with
 * {@code original.replaceWith(replacement)}.  It then updates its {@link BestSoFar.framework.core.helper.VersionInfo}
 * information and generates the {@code Version} for the replacement,
 * which is passed in {@code replacement.fixAsVersion(version)} and ensures the replacement is
 * finalised (i.e. immutable).
 * <p/>
 * An object may be finalised multiple times to update it with new version information.  No new
 * objects are created in doing so, and the only effect is to save teh new version information.
 *
 * @see BestSoFar.framework.core.helper.MutabilityHelper MutabilityHelper
 */
public interface EventuallyImmutable extends Deletable {
    /**
     * Create a mutable clone of this object.  It will remain mutable until passed as a
     * {@code replacement} to {@code replaceWith(replacement)}, at which point
     * {@code fixAsVersion(..)} will be called to effect the change to an immutable object.
     *
     * @return a mutable clone.
     * @throws RuntimeException if this object is still mutable.
     */
    EventuallyImmutable createMutableClone();

    /**
     * If this object is mutable, this method will make it immutable. In any case,
     * it will save the new version information.
     * <p/>
     * Calling {@code replaceWith(replacement)} creates the new
     * {@link BestSoFar.framework.core.helper.VersionInfo version} information and passes it to
     * {@code replacement.fixAsVersion(version)}.
     *
     * @param versionInfo the new version information for this object.
     */
    void fixAsVersion(VersionInfo versionInfo);

    /**
     * Returns true iff this object is mutable, meaning it was created as mutable and has not
     * been finalised.
     *
     * @return true iff this object is mutable.
     */
    boolean isMutable();

    /**
     * Propose a replacement for this object.  How this is handled is determined by the concrete
     * class implementing this.
     * <p/>
     * This must have no next version, and the replacement must have no previous version -
     * otherwise a RuntimeException is thrown.
     * <p/>
     * As an example, a sequence of versions between {@code EventuallyImmutable}
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
    void replaceWith(EventuallyImmutable replacement);

    /**
     * Discards any replacement to this object and ensures this object is not deleted,
     * therefore allowing for it to be replaced by another {@code EventuallyImmutable} object.
     * In doing so, it makes the next version discard its previous version.
     */
    void discardNext();

    /**
     * Discard older versions of this, so they may be freed by the garbage collector.  In doing
     * so it makes the previous version discard its next version.
     */
    void discardPrevious();

    /**
     * Gets the {@link BestSoFar.framework.core.helper.VersionInfo} object that describes this object.  It contains
     * information about this version of the {@code EventuallyImmutable} object,
     * and its earlier and later versions.
     *
     * @return the version information about this object.
     */
    VersionInfo versionInfo();
}

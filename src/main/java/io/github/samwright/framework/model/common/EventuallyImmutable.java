package io.github.samwright.framework.model.common;

import io.github.samwright.framework.model.helper.VersionInfo;

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
 * {@code original.replaceWith(replacement)}.  It then updates its {@link VersionInfo}
 * information and generates the {@code Version} for the replacement,
 * which is passed in {@code replacement.fixAsVersion(version)} and ensures the replacement is
 * finalised (i.e. immutable).
 * <p/>
 * An object may be finalised multiple times to update it with new version information.  No new
 * objects are created in doing so, and the only effect is to save teh new version information.
 *
 * @see io.github.samwright.framework.model.helper.MutabilityHelper MutabilityHelper
 */
public interface EventuallyImmutable extends Replaceable {
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
     * {@link VersionInfo version} information and passes it to
     * {@code replacement.beforeFixAsVersion(version)}.
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


}

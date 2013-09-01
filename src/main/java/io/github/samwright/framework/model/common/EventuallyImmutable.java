package io.github.samwright.framework.model.common;

/**
 * An object which eventually becomes immutable.
 * <p/>
 * It might be immutable from creation, or it might start as mutable and become immutable.  Once
 * immutable, it can not become mutable.
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
 *
 * @see io.github.samwright.framework.model.helper.MutabilityHelper MutabilityHelper
 */
public interface EventuallyImmutable {
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
     * Returns true iff this object is mutable, meaning it was created as mutable and has not
     * been finalised.
     *
     * @return true iff this object is mutable.
     */
    boolean isMutable();
}

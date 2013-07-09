package BestSoFar.framework.immutables.common;

/**
 * User: Sam Wright Date: 04/07/2013 Time: 10:23
 */
public interface EventuallyImmutable {
    /**
     * Create a clone with the given mutability.
     * <p/>
     * A mutable clone can be mutated directly, without interacting with a
     * {@link ReplacementHandler}, and can be turned immutable by calling {@code finalise()}.
     * <p/>
     * Creating an immutable clone has the same effect as creating a mutable clone then
     * finalising it, except aggregated objects can be shared with an immutable clone.
     *
     * @param mutable the initial mutability of the clone.
     * @return a clone with the given initial mutability.
     */
    EventuallyImmutable createClone(boolean mutable);

    /**
     * If this object is mutable, this method will make it immutable.
     */
    void finalise();

    /**
     * Returns true iff this object is mutable, meaning it was created as mutable and has not
     * been finalised.
     *
     * @return true iff this object is mutable.
     */
    boolean isMutable();

    /**
     * Propose a replacement for this object.  How this is handled is determined by the concrete
     * class implementing it.
     * <p/>
     * @param proposed
     * @throws RuntimeException if this object is still mutable.
     */
    void proposeReplacement(EventuallyImmutable proposed);
}

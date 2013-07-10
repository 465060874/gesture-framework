package BestSoFar.framework.core.common;

/**
 * A child of the parent type {@code P}.
 */
public interface ChildOf<P extends ParentOf<? extends ChildOf<P>>> {
    /**
     * Gets the parent that this child sits within.
     * @return the parent that this child sits within.
     */
    P getParent();

    /**
     * Return a version of {@code this} with the given parent.
     * <p/>
     * If this object can be mutated it will be, with {@code this} being returned.
     * Otherwise a clone of {@code this} will be created with the given parent.
     *
     * @param newParent the parent to be given to the returned object.
     * @return a version of {@code this} with the given parent.
     */
    ChildOf<P> withParent(P newParent);
}

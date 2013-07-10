package BestSoFar.framework.core.common;

import java.util.List;

/**
 * A Parent of the child type {@code C}.
 */
public interface ParentOf<C extends ChildOf<? extends ParentOf<C>>> {

    /**
     * Gets the list of children of this parent.
     * @return the list of children of this parent.
     */
    List<C> getChildren();

    /**
     * Return a version of {@code this} with the given children.
     * <p/>
     * If this object can be mutated it will be, with {@code this} being returned.
     * Otherwise a clone of {@code this} will be created with the given children.
     *
     * @param newChildren the children to be given to the returned object.
     * @return a version of {@code this} with the given children.
     */
    ParentOf<C> withChildren(List<C> newChildren);
}

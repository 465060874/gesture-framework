package attempt2;

/**
 * A child of a single parent.
 */
public interface SingleParentChild<T> {
    /**
     * Gets the parent of this object (ie. the object which aggregates this one)
     *
     * @return parent object.
     */
    T getParent();
}

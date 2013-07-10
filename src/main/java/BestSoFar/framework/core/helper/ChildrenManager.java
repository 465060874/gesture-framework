package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.core.common.EventuallyImmutable;
import BestSoFar.framework.core.common.ParentOf;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A helper object that manages a list of children in an {@link EventuallyImmutable} parent-child
 * hierarchy.
 */
public class ChildrenManager<C extends ChildOf<P> & EventuallyImmutable,
                             P extends ParentOf<C> & EventuallyImmutable>
        implements ParentOf<C> {

    @Getter private List<C> children;
    private final P managedParent;

    public ChildrenManager(@NonNull P managedParent) {
        this.managedParent = managedParent;
    }

    public ChildrenManager(@NonNull P managedParent, @NonNull List<C> children) {
        this(managedParent);
        this.children = children;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P withChildren(@NonNull List<C> newChildren) {
        if (managedParent.isMutable()) {
            children = Collections.unmodifiableList(new LinkedList<>(newChildren));
            return managedParent;
        } else {
            P parentClone = (P) managedParent.createMutableClone();
            return (P) parentClone.withChildren(newChildren);
        }
    }

    /**
     * Called when the managed parent has been deleted to delete all its children..
     */
    public void delete() {
        for (C child : children)
            child.delete();
    }

    /**
     * Called when the managed parent is being finalised and is still mutable.
     * <p/>
     * One of four scenarios caused this update:
     * <p/>
     *     1. A child has mutated
     *     <p/>
     *     2. Another property of the this parent has been changed
     *     <p/>
     *     3. A child has been deleted or moved to another parent
     *     <p/>
     *     4. A new child has been added to this parent
     *
     * @param version the new version information.
     */
    @SuppressWarnings("unchecked")
    public void finalise(ImmutableVersion version) {
        List<C> latestChildren = new LinkedList<>(children);

        // Update all children to their latest versions, removing those that were deleted.
        ImmutableVersion.updateAllToLatest(latestChildren);
        children = new LinkedList<>();

        for (C child : latestChildren) {
            if (child.getParent() == version.getPrevious()) {
                // If the child still thinks of managedParent's previous version as its parent,
                // create a version of the child with the managedParent as the parent
                C newChild = (C) child.withParent(managedParent);
                child.replaceWith(newChild);
                children.add(newChild);
            } else if (child.getParent() == managedParent) {
                // Otherwise the child has already been created pointing to managedParent as its
                // parent, so keep it as is.
                children.add(child);
            }
        }

        children = Collections.unmodifiableList(children);
    }
}

package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ChildOf;
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

    /**
     * Constructs a {@code ChildrenManager} with an empty children list.
     *
     * @param managedParent the parent who's children this will manage.
     */
    public ChildrenManager(P managedParent) {
        this(managedParent, Collections.<C>emptyList());
    }

    /**
     * Constructs a {@code ChildrenManager} with the given children list.  The list won't be
     * mutated (i.e. it is fine for children to be {@code Collections.unmodifiableList}).
     *
     * @param managedParent the parent who's children this will manage.
     * @param children
     */
    public ChildrenManager(@NonNull P managedParent, @NonNull List<C> children) {
        this.managedParent = managedParent;
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
     * Called when the managed parent is being fixed and is still mutable.
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
     * @param versionInfo the new version information.
     */
    @SuppressWarnings("unchecked")
    public void fixAsVersion(VersionInfo versionInfo) {
        List<C> latestChildren = new LinkedList<>(children);

        // Update all children to their latest versions, removing those that were deleted.
        VersionInfo.updateAllToLatest(latestChildren);
        children = new LinkedList<>();

        for (C child : latestChildren) {
            if (child.getParent() == versionInfo.getPrevious()) {
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

    /**
     * Called when the managed parent is having its replacement discarded,
     * and discards all of its childrens' replacements.
     */
    public void discardReplacement() {
        for (C child : children)
            child.discardReplacement();
    }

    /**
     * Called when the managed parent is having its older versions discarded,
     * and discards all of its childrens' older versions.
     */
    public void discardOlderVersions() {
        for (C child : children)
            child.discardOlderVersions();
    }
}

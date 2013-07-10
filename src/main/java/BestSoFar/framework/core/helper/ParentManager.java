package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.EventuallyImmutable;
import BestSoFar.framework.core.common.ParentOf;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * A helper object that manages a child's parent in an {@link EventuallyImmutable} parent-child
 * hierarchy.
 */
public class ParentManager<C extends ChildOf<P> & EventuallyImmutable,
                           P extends ParentOf<C> & EventuallyImmutable>
        implements ChildOf<P> {

    @Getter private P parent;
    private final C managedChild;

    /**
     * Create a {@code ParentManager} to manage the given child's parent.
     *
     * @param managedChild the child who's parent this will manage.
     */
    public ParentManager(@NonNull C managedChild) {
        this.managedChild = managedChild;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withParent(P newParent) {
        if (getParent() == newParent)
            return managedChild;

        if (managedChild.isMutable()) {
            this.parent = newParent;
            return managedChild;
        }

        if (newParent != null)
            newParent = (P) newParent.versionInfo().getLatest();

        C childClone = (C) managedChild.createMutableClone();
        return (C) childClone.withParent(newParent);
    }

    /**
     * Called when the managed child has been deleted.
     * <p/>
     * If the child has a parent that wasn't deleted, this method removes the child from it.
     */
    @SuppressWarnings("unchecked")
    public void delete() {
        if (getParent() != null && !getParent().isDeleted()) {
            List<C> siblings = new LinkedList<>(getParent().getChildren());
            siblings.remove(managedChild);
            P newParent = (P) getParent().createMutableClone();
            newParent = (P) newParent.withChildren(siblings);
            getParent().replaceWith(newParent);
        }
    }

    /**
     * Called when the managed child is being fixed and is still mutable.
     * <p/>
     * One of two scenarios caused this update
     * <p/>
     *     1. A property of the parent was changed
     *     <p/>
     *     2. A property of the managed child was changed
     *
     * @param versionInfo the new version information.
     */
    @SuppressWarnings("unchecked")
    public void fixAsVersion(VersionInfo versionInfo) {
        // In scenario 1, the parent is updating its children to point to the new version of the
        // parent.  There's nothing to do!

        // In scenario 2, the parent remains its old, immutable version.  We must create a new
        // parent given the updated list of children.
        if (getParent() != null && !getParent().isMutable()) {
            List<C> newSiblings = new LinkedList<>(getParent().getChildren());
            VersionInfo.updateAllToLatest(newSiblings);
            if (!newSiblings.contains(managedChild))
                newSiblings.add(managedChild);

            P oldParent = parent;
            parent = (P) getParent().withChildren(newSiblings);
            oldParent.replaceWith(parent);
        }
    }
}

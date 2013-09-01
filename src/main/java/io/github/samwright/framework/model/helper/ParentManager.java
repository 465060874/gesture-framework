package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import io.github.samwright.framework.model.common.ParentOf;
import io.github.samwright.framework.model.common.Replaceable;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A helper object that manages a child's parent in an {@link EventuallyImmutable} parent-child
 * hierarchy.
 */
public class ParentManager<C extends ChildOf<P> & Processor,
                           P extends ParentOf<C> & Processor>
        implements ChildOf<P> {

    @Getter private P parent;
    private final C managedChild;

    /**
     * Create a {@code ParentManager} to manage the given child's parent,
     * which is initially set to null.
     *
     * @param managedChild the child who's parent this will manage.
     */
    public ParentManager(@NonNull C managedChild) {
        this(managedChild, null);
    }

    /**
     * Create a {@code ParentManage} to manage the given child's given {@code parent}.
     *
     * @param managedChild the child who's parent this will manage.
     * @param parent the child's parent.
     */
    public ParentManager(@NonNull C managedChild, P parent) {
        this.managedChild = managedChild;
        this.parent = parent;
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

        C childClone = (C) managedChild.createMutableClone();

        return (C) childClone.withParent(newParent);
    }

    /**
     * Called before the managed child replaces another child.
     * <p/>
     * One of two scenarios caused this update
     * <p/>
     *     1. A property of the parent was changed
     *     <p/>
     *     2. A property of the managed child was changed
     *
     * @param toReplace the child that managedChild will replace
     */
    @SuppressWarnings("unchecked")
    public void beforeReplacing(C toReplace) {
        if (!managedChild.isMutable())
            return;

        // In scenario 1, the parent is updating its children to point to the new version of the
        // parent.  There's nothing to do!

        // In scenario 2, the parent remains its old, immutable version.  We must create a new
        // parent given the updated list of children.
        if (getParent() != null && !getParent().isMutable()) {
            List<C> newSiblings = new LinkedList<>(getParent().getChildren());
//            VersionInfo.updateAllToLatest(newSiblings);
            if (!newSiblings.contains(managedChild)
                    && (toReplace == null || !newSiblings.contains(toReplace))) {
                newSiblings.add(managedChild);
            }

            P oldParent = parent;
            parent = (P) getParent().withChildren(newSiblings);
            oldParent.replaceWith(parent);
        }
    }

    public void orphanChild() {
        List<C> newChildren = new ArrayList<>(parent.getChildren());
        newChildren.remove(managedChild);
        parent.replaceWith((Replaceable) parent.withChildren(newChildren));
    }

    /**
     * Called when the managed child is having its replacement discarded,
     * and tells its parent to discard its replacement.
     */
    public void discardNext() {
        if (parent != null && parent.getNext() != null
                && !parent.getNext().isMutable())
            parent.discardNext();
    }

    /**
     * Called when the managed child is having its older versions discarded,
     * and tells its parent to discard its older versions.
     */
    public void discardPrevious() {
        if (parent != null && parent.getPrevious() != null)
            parent.discardPrevious();
    }

    /**
     * Called when the managed child is being set as the current version, and instructs its
     * parent to do the same.
     */
    public void setAsCurrentVersion() {
        if (parent != null)
            parent.setAsCurrentVersion();
    }
}

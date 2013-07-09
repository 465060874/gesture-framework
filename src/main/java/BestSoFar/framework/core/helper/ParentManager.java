package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.ParentOf;
import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.immutables.ImmutableVersion;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 05/07/2013 Time: 11:00
 */
public class ParentManager<C extends ChildOf<P> & EventuallyImmutable & Deletable,
                           P extends ParentOf<C> & EventuallyImmutable & Deletable>
        implements ChildOf<P> {
    @Getter private P parent;
    private final C managedChild;

    public ParentManager(C managedChild) {
        this.managedChild = managedChild;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withParent(P parent) {
        if (getParent() == parent)
            return managedChild;

        if (managedChild.isMutable()) {
            this.parent = parent;
            return managedChild;
        }

        parent = (P) parent.getVersion().getLatest();
        C childClone = (C) managedChild.createMutableClone();
        return (C) childClone.withParent(parent);
    }

    @SuppressWarnings("unchecked")
    public void delete() {
        if (!getParent().isDeleted()) {
            List<C> siblings = new LinkedList<>(getParent().getChildren());
            siblings.remove(managedChild);
            P newParent = (P) getParent().createMutableClone();
            newParent = (P) newParent.withChildren(siblings);
            getParent().replaceWith(newParent);
        }
    }

    @SuppressWarnings("unchecked")
    public void finalise(ImmutableVersion version) {
        if (!getParent().isMutable()) {

            List<C> newSiblings = new LinkedList<>(getParent().getChildren());
            ImmutableVersion.updateAllToLatest(newSiblings);
            if (!newSiblings.contains(managedChild))
                newSiblings.add(managedChild);

            parent = (P) getParent().withChildren(newSiblings);
        }
    }
}

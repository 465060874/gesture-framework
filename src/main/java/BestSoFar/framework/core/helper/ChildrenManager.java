package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Element;
import BestSoFar.framework.core.ParentOf;
import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.immutables.ImmutableVersion;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 09/07/2013 Time: 16:25
 */
public class ChildrenManager<C extends ChildOf<P> & EventuallyImmutable & Deletable,
                             P extends ParentOf<C> & EventuallyImmutable & Deletable>
        implements ParentOf<C> {

    @Getter private List<C> children;
    private final P managedParent;

    public ChildrenManager(P managedParent) {
        this.managedParent = managedParent;
    }

    public ChildrenManager(P managedParent, List<C> children) {
        this(managedParent);
        this.children = children;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P withChildren(List<C> newChildren) {
        if (managedParent.isMutable()) {
            children = Collections.unmodifiableList(new LinkedList<>(newChildren));
            return managedParent;
        } else {
            P parentClone = (P) managedParent.createMutableClone();
            return (P) parentClone.withChildren(newChildren);
        }
    }

    public void delete() {
        for (C child : children)
            child.delete();
    }

    @SuppressWarnings("unchecked")
    public void finalise(ImmutableVersion version) {
        List<C> latestChildren = new LinkedList<>(children);
        ImmutableVersion.updateAllToLatest(latestChildren);
        children = new LinkedList<>();
        for (C child : latestChildren)
            if (child.getParent() == version.getPrevious() ||
                    child.getParent() == this) {
                C newChild = (C) child.withParent(managedParent);
                child.replaceWith(newChild);
                children.add(newChild);
            }
        children = Collections.unmodifiableList(children);
    }
}

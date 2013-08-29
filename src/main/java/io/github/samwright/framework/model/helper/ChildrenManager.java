package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import io.github.samwright.framework.model.common.ParentOf;
import lombok.Getter;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

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
            if (new HashSet<>(newChildren).size() != newChildren.size())
                throw new RuntimeException("Supplied children list contains duplicates");

            children = Collections.unmodifiableList(new LinkedList<>(newChildren));
            return managedParent;
        } else {
            P parentClone = (P) managedParent.createMutableClone();
            return (P) parentClone.withChildren(newChildren);
        }
    }

    /**
     * Called after the managed parent has been deleted, to delete all its children..
     */
    public void afterDelete() {
        for (C child : children)
            child.delete();
    }

    /**
     * Called before the managed parent is being fixed.
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
    public void beforeFixAsVersion(VersionInfo versionInfo) {
        if (!managedParent.isMutable())
            return;

        List<C> latestChildren = new LinkedList<>();

        for (C child : children) {
            if (child.isDeleted())
                continue;

            C childNextVersion = (C) child.versionInfo().getNext();
            if (childNextVersion == null) {
                if (child.getParent() == managedParent) {
                    // Child is a new addition to this parent - just need to add it to list.
                    latestChildren.add(child);
                    if (!child.isBeingFixed())
                        // Being loaded from XML - I need to fix it myself.
                        child.fixAsVersion(child.versionInfo());

                } else {
                    // Child has not been updated.  Grandfather it in to the new version:
                    C newChild = (C) child.withParent(managedParent);
                    child.replaceWith(newChild);
                    latestChildren.add(newChild);
                }
            } else {
                if (childNextVersion.getParent() == managedParent)
                    // Child replaced itself from managedParent to managedParent.
                    latestChildren.add(childNextVersion);
                // Otherwise, Child has been replaced and has been given to another parent,
                // so don't include it.
            }
        }

        children = Collections.unmodifiableList(latestChildren);
    }

    /**
     * Called when the managed parent is having its replacement discarded,
     * and discards all of its childrens' replacements.
     */
    public void discardNext() {
        for (C child : children)
            if (child.versionInfo().getNext() != null)
                child.discardNext();
    }

    /**
     * Called when the managed parent is having its older versions discarded,
     * and discards all of its childrens' older versions.
     */
    public void discardPrevious() {
        for (C child : children)
            if (child.versionInfo().getPrevious() != null)
                child.discardPrevious();
    }

    /**
     * Called when the managed parent is being set as the current version,
     * and instructs its children to do the same.
     */
    public void setAsCurrentVersion() {
        for (C child : children)
            child.setAsCurrentVersion();
    }

    public void afterVersionFixed() {
        for (C child : children)
            child.afterVersionFixed();
    }

    public Element getXMLForDocument(Document doc) {
        Element childrenNode = doc.createElement("Children");

        for (C child : children)
            if (child instanceof Processor) {
                Element childNode = ((Processor) child).getXMLForDocument(doc);
                childrenNode.appendChild(childNode);
            }

        return childrenNode;
    }

    @SuppressWarnings("unchecked")
    public void withXML(Element node, Map<UUID, Processor> dictionary) {
        if (!managedParent.isMutable())
            throw new RuntimeException("Should only be run when mutable");

        children = new ArrayList<>();
        Element childrenNode = XMLHelper.getFirstChildWithName(node, "Children");

        for (Element childNode : XMLHelper.iterator(childrenNode)) {
            C child = (C) ModelLoader.getPrototypeModel(childNode);
            ((Processor) child).withXML(childNode, dictionary);
            child.withParent(managedParent);
            children.add(child);
        }
    }
}

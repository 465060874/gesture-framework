package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.Replaceable;
import io.github.samwright.framework.model.helper.ChildrenManager;
import io.github.samwright.framework.model.helper.ParentManager;
import io.github.samwright.framework.model.helper.TypeData;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract implementation of {@link Workflow}.  Manages its {@link WorkflowContainer} parent,
 * the list of {@link Element} children in this, its {@link TypeData}, and mutation management.
 */
public abstract class AbstractWorkflow extends AbstractProcessor implements Workflow {
    private final ParentManager<Workflow, WorkflowContainer> parentManager;
    private final ChildrenManager<Element, Workflow> childrenManager;

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflow}.
     */
    public AbstractWorkflow() {
        childrenManager = new ChildrenManager<Element, Workflow>(this);
        parentManager = new ParentManager<>((Workflow) this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractWorkflow} with the given
     * {@link TypeData}.
     *
     * @param oldWorkflow the {@code AbstractWorkflow} to clone.
     */
    public AbstractWorkflow(AbstractWorkflow oldWorkflow) {
        super(oldWorkflow);
        childrenManager = new ChildrenManager<Element, Workflow>(this, oldWorkflow.getChildren());
        parentManager = new ParentManager<Workflow,WorkflowContainer>(this, oldWorkflow.getParent());
    }

    @Override
    public WorkflowContainer getParent() {
        return parentManager.getParent();
    }

    @Override
    public List<Element> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    public Workflow withChildren(List<Element> newChildren) {
        return childrenManager.withChildren(newChildren);
    }

    @Override
    public Workflow withParent(WorkflowContainer newParent) {
        if (newParent != null && getParent() != null
                && !getTypeData().equals(getParent().getTypeData()))
            return withTypeData(newParent.getTypeData()).withParent(newParent);
        else
            return parentManager.withParent(newParent);
    }

    @Override
    public void delete() {
        super.delete();
        parentManager.orphanChild();
    }

    @Override
    public void discardNext() {
        super.discardNext();
        childrenManager.discardNext();
        parentManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        super.discardPrevious();
        childrenManager.discardPrevious();
        parentManager.discardPrevious();
    }

    @Override
    public void setAsCurrentVersion() {
        if (this != getCurrentVersion()) {
            super.setAsCurrentVersion();
            parentManager.setAsCurrentVersion();
            childrenManager.setAsCurrentVersion();
        }
    }

    @Override
    public void replace(Replaceable toReplace) {
        childrenManager.beforeReplacing((Workflow) toReplace);
        parentManager.beforeReplacing((Workflow) toReplace);
        super.replace(toReplace);
    }

    @Override
    public void afterReplacement() {
        childrenManager.afterReplacement();
    }

    @Override
    public Workflow withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return (Workflow) createMutableClone().withXML(node, map);
        super.withXML(node, map);
        withParent(null);
        childrenManager.withXML(node, map);

        return this;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        node.appendChild(childrenManager.getXMLForDocument(doc));
        return node;
    }

    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }

    @Override
    public String getModelIdentifier() {
        return Workflow.class.getName();
    }

    @Override
    public Workflow getCurrentVersion() {
        return (Workflow) super.getCurrentVersion();
    }

    @Override
    public Workflow withTypeData(TypeData typeData) {
        return (Workflow) super.withTypeData(typeData);
    }

    @Override
    public String getXMLTag() {
        return "Workflow";
    }
}

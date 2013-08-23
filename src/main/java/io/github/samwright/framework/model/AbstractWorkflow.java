package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.*;
import lombok.Delegate;

import java.util.List;

/**
 * Abstract implementation of {@link Workflow}.  Manages its {@link WorkflowContainer} parent,
 * the list of {@link Element} children in this, its {@link TypeData}, and mutation management.
 */
public abstract class AbstractWorkflow implements Workflow {
    private final ParentManager<Workflow, WorkflowContainer> parentManager;
    private final ChildrenManager<Element, Workflow> childrenManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper<Workflow> mutabilityHelper;

    @Delegate
    private final TypeDataManager<Workflow> typeDataManager;

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflow}.
     */
    public AbstractWorkflow() {
        typeDataManager = new TypeDataManager<Workflow>(this);
        mutabilityHelper = new MutabilityHelper<>((Workflow) this, false);
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
        if (oldWorkflow.isMutable())
            throw new RuntimeException("Cannot clone an immutable object");

        typeDataManager = new TypeDataManager<Workflow>(this, oldWorkflow.getTypeData());
        mutabilityHelper = new MutabilityHelper<>((Workflow) this, true);
        childrenManager = new ChildrenManager<Element, Workflow>(this, oldWorkflow.getChildren());
        parentManager = new ParentManager<Workflow,WorkflowContainer>
                (this, oldWorkflow.getParent());
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
    public void discardNext() {
        mutabilityHelper.discardNext();
        childrenManager.discardNext();
        parentManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        mutabilityHelper.discardPrevious();
        childrenManager.discardPrevious();
        parentManager.discardPrevious();
    }

    @Override
    public void redo() {
        mutabilityHelper.redo();
        childrenManager.redo();
    }

    @Override
    public void undo() {
        mutabilityHelper.undo();
        childrenManager.undo();
    }

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.afterDelete();
        childrenManager.afterDelete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        childrenManager.beforeFixAsVersion(versionInfo);
        parentManager.beforeFixAsVersion(versionInfo);

        mutabilityHelper.fixAsVersion(versionInfo);
    }

    @Override
    public Workflow createOrphanedDeepClone() {
        Workflow clone = this.withParent(null);
        this.replaceWith(clone);
        this.discardNext();
        return clone;
    }


    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }
}

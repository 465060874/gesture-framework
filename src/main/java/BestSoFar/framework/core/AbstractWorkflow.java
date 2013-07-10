package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.*;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Abstract implementation of Workflow.  Manages parent, the list of elements in the workflow,
 * typedata, and mutation management.
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Getter @NonNull private final TypeData<I, O> typeData;
    private final ParentManager<Workflow<I, O>, WorkflowContainer<I, O>> parentManager;
    private final ChildrenManager<Element<?, ?>, Workflow<?, ?>> childrenManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper mutabilityHelper;

    {
        parentManager = new ParentManager<>((Workflow<I, O>) this);
    }

    public AbstractWorkflow(TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, false);
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this);
    }

    @SuppressWarnings("unchecked")
    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, true);
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this, oldWorkflow.getChildren());
    }

    @Override
    public WorkflowContainer<I, O> getParent() {
        return parentManager.getParent();
    }

    @Override
    public List<Element<?, ?>> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workflow<I, O> withChildren(List<Element<?, ?>> newChildren) {
        return (Workflow<I, O>) childrenManager.withChildren(newChildren);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workflow<I, O> withParent(WorkflowContainer<I, O> newParent) {
        if (newParent != null && !typeData.equals(getParent().getTypeData())) {
            String msg = String.format(
                    "Workflow%s must have same type data as WorkflowContainer%s",
                    typeData.toString(),
                    getParent().getTypeData().toString()
            );
            throw new ClassCastException(msg);
        }

        return parentManager.withParent(newParent);
    }

    @Override
    abstract public AbstractWorkflow<I, O> createMutableClone();

    @Override
    public void discardReplacement() {
        mutabilityHelper.discardReplacement();
        childrenManager.discardReplacement();
    }

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.delete();
        childrenManager.delete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        if (isMutable()) {
            parentManager.finalise(versionInfo);
            childrenManager.finalise(versionInfo);
        }
        mutabilityHelper.fixAsVersion(versionInfo);
    }
}

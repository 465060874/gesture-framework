package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.*;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Abstract implementation of {@link Workflow}.  Manages its {@link WorkflowContainer} parent,
 * the list of {@link Element} children in this, its {@link TypeData}, and mutation management.
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Getter @NonNull private final TypeData<I, O> typeData;
    private final ParentManager<Workflow<I, O>, WorkflowContainer<I, O>> parentManager;
    private final ChildrenManager<Element<?, ?>, Workflow<?, ?>> childrenManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper mutabilityHelper;

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflow} with the given
     * {@link TypeData}.
     *
     * @param typeData the input/output types of this object.
     */
    public AbstractWorkflow(TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, false);
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this);
        parentManager = new ParentManager<>((Workflow<I, O>) this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractWorkflow} with the given
     * {@link TypeData}.
     *
     * @param oldWorkflow the {@code AbstractWorkflow} to clone.
     * @param typeData the input/output types of this object.
     */
    @SuppressWarnings("unchecked")
    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow, TypeData<I, O> typeData) {
        if (oldWorkflow.isMutable())
            throw new RuntimeException("Cannot clone an immutable object");
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, true);
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this, oldWorkflow.getChildren());
        parentManager = new ParentManager<>((Workflow<I, O>) this, oldWorkflow.getParent());
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
    public void discardNext() {
        mutabilityHelper.discardNext();
        childrenManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        mutabilityHelper.discardPrevious();
        childrenManager.discardPrevious();
    }

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.afterDelete();
        childrenManager.afterDelete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        parentManager.beforeFixAsVersion(versionInfo);
        childrenManager.beforeFixAsVersion(versionInfo);

        mutabilityHelper.fixAsVersion(versionInfo);
    }
}

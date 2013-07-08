package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.immutables.ImmutableList;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.ImmutableSet;
import BestSoFar.framework.immutables.SelfReplacingImmutableImpl;
import BestSoFar.framework.immutables.common.Immutable;
import BestSoFar.framework.immutables.common.SelfReplacingImmutable;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

/**
 * Abstract implementation of Workflow.  Manages parent, the list of elements in the workflow,
 * typedata, and mutation management.
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Getter private WorkflowContainer<I, O> parent;
    @Getter private ImmutableList<Element<?, ?>> elements;
    @Getter @NonNull private final TypeData<I, O> typeData;

    @Delegate(excludes = SelfReplacingImmutableImpl.ToOverride.class)
    private final SelfReplacingImmutableImpl replacementManager;


    public AbstractWorkflow(TypeData<I, O> typeData, boolean mutable) {
        elements = new ImmutableList<>(false);
        elements.assignToHandler(this);
        replacementManager = new SelfReplacingImmutableImpl(mutable);
        this.typeData = typeData;
    }

    @SuppressWarnings("unchecked")
    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow,
                            TypeData<I, O> typeData, boolean mutable) {
        this.typeData = typeData;
        elements = oldWorkflow.elements.createClone(false);
        elements.assignToHandler(this);
        replacementManager = new SelfReplacingImmutableImpl(mutable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParent(WorkflowContainer<I, O> parent) {
        if (parent != null && !typeData.equals(getParent().getTypeData())) {
            String msg = String.format(
                    "Workflow%s must have same type data as WorkflowContainer%s",
                    typeData.toString(),
                    getParent().getTypeData().toString()
            );
            throw new ClassCastException(msg);
        }

        if (isMutable())
            this.parent = parent;
        else {
            AbstractWorkflow<I, O> replacement = createClone(true);
            replacement.setParent((WorkflowContainer<I,O>) parent.getLatest());
            proposeReplacement(replacement);
        }
    }

    @Override
    abstract public AbstractWorkflow<I, O> createClone(boolean mutable);

    @Override
    public void delete() {
        replacementManager.delete();

        if (!parent.isDeleted())
            getParent().getWorkflows().remove(this);

        for (Element<?, ?> element : elements)
            element.delete();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReplacement(Immutable existingObject, Immutable proposedObject) {
        if (elements == existingObject) {
            if (isMutable()) {
                elements = (ImmutableList<Element<?, ?>>) proposedObject;
            } else {
                AbstractWorkflow<I, O> replacement = createClone(true);
                replacement.elements = (ImmutableList<Element<?,?>>) proposedObject;
                replacement.elements.assignToHandler(replacement);
                proposeReplacement(replacement);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void finalise() {
        // TODO: This repeats AbstractElement.finalise()
        if (!parent.isMutable())
            parent.getWorkflows().replaceOrAdd(getReplaced(), this);


        // TODO: This repeats AbstractWorkflowContainer.finalise()
        ImmutableList<Element<?, ?>> updatedElements = new ImmutableList<>(true);
        elements.assignToHandler(this);

        for (Element element : elements) {
            element = (Element) element.getLatest();
            if (element.getParent() == getReplaced()) {
                element.setParent(this);
                element = (Element) element.getLatest();
                updatedElements.add(element);
            } else if (element.getParent() == this) {
                updatedElements.add(element);
            }
        }

        elements = updatedElements;
        elements.assignToHandler(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractWorkflow<I, O> getReplaced() {
        return (AbstractWorkflow<I, O>) replacementManager.getReplaced();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractWorkflow<I, O> getReplacement() {
        return (AbstractWorkflow<I, O>) replacementManager.getReplacement();
    }

}

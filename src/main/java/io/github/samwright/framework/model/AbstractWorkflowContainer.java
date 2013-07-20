package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.ChildrenManager;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.VersionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link WorkflowContainer}.
 * <p/>
 * Concrete {@code WorkflowContainer} implementations can derive from this to let it handle the
 * list of {@link Workflow} objects in this, along with everything handled in
 * {@link AbstractElement}.
 */
public abstract class AbstractWorkflowContainer<I, O>
        extends AbstractElement<I, O> implements WorkflowContainer<I, O> {

    private final ChildrenManager<Workflow<I, O>, WorkflowContainer<I, O>> childrenManager;

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflowContainer} with the given
     * {@link TypeData}.
     *
     * @param typeData the input/output types of this object.
     */
    public AbstractWorkflowContainer(TypeData<I, O> typeData) {
        super(typeData);
        childrenManager = new ChildrenManager<>((WorkflowContainer<I, O>) this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractWorkflowContainer} with the given
     * {@link TypeData}.
     *
     * @param oldWorkflowContainer the {@code AbstractWorkflowContainer} to clone.
     * @param typeData the input/output types of this object.
     */
    @SuppressWarnings("unchecked")
    public AbstractWorkflowContainer(AbstractWorkflowContainer<?, ?> oldWorkflowContainer,
                                     TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
        childrenManager = new ChildrenManager<Workflow<I,O>, WorkflowContainer<I, O>>(
                this,
                (List<Workflow<I,O>>) (List<?>) oldWorkflowContainer.getChildren());
    }

    @Override
    public boolean isValid() {
        if (getChildren().isEmpty())
            return false;

        for (Workflow<I, O> workflow : getChildren()) {
            if (!workflow.isValid())
                return false;
        }

        return true;
    }

    @Override
    public WorkflowContainer<I, O> withParent(Workflow<?, ?> newParent) {
        return (WorkflowContainer<I, O>) super.withParent(newParent);
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        List<Mediator<O>> outputs = new ArrayList<>();

        for (Workflow<I, O> workflow : getChildren())
            outputs.addAll(workflow.processTrainingBatch(inputs));

        return outputs;
    }

    @Override
    public void discardNext() {
        super.discardNext();
        childrenManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        super.discardPrevious();
        childrenManager.discardPrevious();
    }

    @Override
    public void delete() {
        super.delete();
        childrenManager.afterDelete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        childrenManager.beforeFixAsVersion(versionInfo);
        super.fixAsVersion(versionInfo);
    }

    @Override
    public AbstractWorkflowContainer<I, O> createMutableClone() {
        return (AbstractWorkflowContainer<I, O>) withTypeData(getTypeData());
    }

    @Override
    public List<Workflow<I, O>> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    public WorkflowContainer<I, O> withChildren(List<Workflow<I, O>> newChildren) {
        return childrenManager.withChildren(newChildren);
    }
}

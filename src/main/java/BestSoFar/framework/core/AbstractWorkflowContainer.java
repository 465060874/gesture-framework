package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.immutables.ImmutableList;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.common.Immutable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of {@link WorkflowContainer}.
 * <p/>
 * Concrete WorkflowContainer implementations can derive from this to let it handle the internal
 * list of {@link Workflow} objects (and all the cloning that comes from modifications to it),
 * along with boilerplate code (accessors for parent and {@link TypeData}, and
 * {@link BestSoFar.framework.core.common.ProcessObserver} management).
 */
public abstract class AbstractWorkflowContainer<I, O>
        extends AbstractElement<I, O> implements WorkflowContainer<I, O> {

    @Getter private ImmutableList<Workflow<I, O>> workflows;

    public AbstractWorkflowContainer(TypeData<I, O> typeData, boolean mutable) {
        super(typeData, mutable);
        workflows = new ImmutableList<>(false);
        workflows.assignToHandler(this);
    }

    @SuppressWarnings("unchecked")
    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer,
                                     TypeData<I, O> typeData, boolean mutable) {
        super(oldWorkflowContainer, typeData, mutable);
        workflows = oldWorkflowContainer.workflows.createClone(false);
        workflows.assignToHandler(this);
    }

    @Override
    public boolean isValid() {
        for (Workflow<I, O> workflow : workflows) {
            if (!workflow.isValid())
                return false;
        }

        return true;
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        List<Mediator<O>> outputs = new ArrayList<>();

        for (Workflow<I, O> workflow : workflows)
            outputs.addAll(workflow.processTrainingBatch(inputs));

        return outputs;
    }

    @Override
    public void delete() {
        super.delete();

        for (Workflow<I, O> workflow : workflows)
            workflow.delete();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void finalise() {
        super.finalise();

        ImmutableList<Workflow<I, O>> updatedWorkflows = new ImmutableList<>(true);
        workflows.assignToHandler(this);

        for (Workflow<I, O> workflow : workflows) {
            workflow = (Workflow<I, O>) workflow.getLatest();
            if (workflow.getParent() == getReplaced()) {
                workflow.setParent(this);
                workflow = (Workflow<I, O>) workflow.getLatest();
                updatedWorkflows.add(workflow);
            } else if (workflow.getParent() == this) {
                updatedWorkflows.add(workflow);
            }
        }

        workflows = updatedWorkflows;
        workflows.assignToHandler(this);
    }

    @Override
    public AbstractWorkflowContainer<I, O> getReplaced() {
        return (AbstractWorkflowContainer<I, O>) super.getReplaced();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReplacement(Immutable existingObject, Immutable proposedObject) {
        super.handleReplacement(existingObject, proposedObject);

        if (workflows == existingObject) {
            if (isMutable()) {
                workflows = (ImmutableList<Workflow<I, O>>) proposedObject;
            } else {
                AbstractWorkflowContainer<I, O> replacement = createClone(true);
                replacement.workflows = (ImmutableList<Workflow<I,O>>) proposedObject;
                replacement.workflows.assignToHandler(replacement);
                proposeReplacement(replacement);
            }
        }
    }

    @Override
    public abstract AbstractWorkflowContainer<I, O> createClone(boolean mutable);

    @Override
    public AbstractWorkflowContainer<I, O> getReplacement() {
        return (AbstractWorkflowContainer<I, O>) super.getReplacement();
    }
}

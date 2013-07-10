package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.ChildrenManager;
import BestSoFar.framework.core.helper.VersionInfo;
import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.core.helper.TypeData;

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

    public AbstractWorkflowContainer(TypeData<I, O> typeData) {
        super(typeData);
        childrenManager = new ChildrenManager<>((WorkflowContainer<I, O>) this);
    }

    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer,
                                     TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
        childrenManager = new ChildrenManager<>((WorkflowContainer<I, O>) this,
                oldWorkflowContainer.getChildren());
    }

    @Override
    public boolean isValid() {
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
    public void discardReplacement() {
        super.discardReplacement();
        childrenManager.discardReplacement();
    }

    @Override
    public void discardOlderVersions() {
        super.discardOlderVersions();
        childrenManager.discardOlderVersions();
    }

    @Override
    public void delete() {
        super.delete();
        childrenManager.delete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        if (isMutable())
            childrenManager.fixAsVersion(versionInfo);

        super.fixAsVersion(versionInfo);
    }

    @Override
    public abstract AbstractWorkflowContainer<I, O> createMutableClone();

    @Override
    public List<Workflow<I, O>> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    public WorkflowContainer<I, O> withChildren(List<Workflow<I, O>> newChildren) {
        return childrenManager.withChildren(newChildren);
    }
}

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
public abstract class AbstractWorkflowContainer
        extends AbstractElement implements WorkflowContainer {

    private final ChildrenManager<Workflow, WorkflowContainer> childrenManager;

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflowContainer}.
     */
    public AbstractWorkflowContainer() {
        super();
        childrenManager = new ChildrenManager<Workflow, WorkflowContainer>(this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractWorkflowContainer}.
     *
     * @param oldWorkflowContainer the {@code AbstractWorkflowContainer} to clone.
     */
    public AbstractWorkflowContainer(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
        childrenManager =
                new ChildrenManager<Workflow, WorkflowContainer>
                (this, oldWorkflowContainer.getChildren());
    }

    @Override
    public boolean isValid() {
        if (getChildren().isEmpty())
            return false;

        for (Workflow workflow : getChildren()) {
            if (!workflow.isValid())
                return false;
        }

        return true;
    }

    @Override
    public WorkflowContainer withParent(Workflow newParent) {
        return (WorkflowContainer) super.withParent(newParent);
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        List<Mediator> outputs = new ArrayList<>();

        for (Workflow workflow : getChildren())
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
    public List<Workflow> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    public WorkflowContainer withChildren(List<Workflow> newChildren) {
        return childrenManager.withChildren(newChildren);
    }

    @Override
    public WorkflowContainer withTypeData(TypeData typeData) {
        return (WorkflowContainer) super.withTypeData(typeData);
    }

    @Override
    public WorkflowContainer createOrphanedDeepClone() {
        return (WorkflowContainer) super.createOrphanedDeepClone();
    }
}

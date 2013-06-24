package attempt2;

import lombok.Delegate;

import java.util.Collections;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:22
 */
public abstract class WorkflowContainer<I, O> extends ObservableProcessor<I, O>
        implements ImmutableList<Workflow<I, O>>, Element<I, O>, ImmutableListHandler {

    @Delegate(types = ImmutableList.class)
    private final ImmutableListImpl<Workflow<I, O>> workflows;

    @Override
    public void handleNewList() {
        WorkflowContainer<I, O> nextContainer = (WorkflowContainer<I, O>) callCopyConstructor();
        getParent().replace(this, nextContainer);
    }

    public WorkflowContainer() {
        workflows = new ImmutableListImpl<>(Collections.<Workflow<I, O>>emptyList(), this);
    }

    public WorkflowContainer(WorkflowContainer<I, O> oldWorkflowContainer) {
        workflows = new ImmutableListImpl<>(oldWorkflowContainer.workflows.getNextList(), this);
    }


}

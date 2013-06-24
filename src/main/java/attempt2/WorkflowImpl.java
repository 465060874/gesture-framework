package attempt2;

import lombok.Delegate;

import java.util.Collections;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:34
 */
public class WorkflowImpl<I, O> extends ObservableProcessor<I, O> implements Workflow<I, O>, ImmutableListHandler {

    @Delegate(types = ImmutableList.class)
    private final ImmutableListImpl<Element<?, ?>> elements;

    @Override
    public void handleNewList() {
        WorkflowContainer<I, O> nextContainer = (WorkflowContainer<I, O>) callCopyConstructor();
        getParent().replace(this, nextContainer);
    }

    public WorkflowImpl() {
        elements = new ImmutableListImpl<>(Collections.<Element<?, ?>>emptyList(), this);
    }

    public WorkflowImpl(WorkflowImpl<I, O> oldWorkflow) {
        elements = new ImmutableListImpl<>(oldWorkflow.elements.getNextList(), this);
    }
}

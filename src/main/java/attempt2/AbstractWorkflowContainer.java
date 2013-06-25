package attempt2;

import attempt2.FailedLists.ImmutableList;
import attempt2.FailedLists.ImmutableListImpl;
import lombok.Delegate;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:22
 */
public abstract class AbstractWorkflowContainer<I, O> implements WorkflowContainer<I,O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    private final ImmutableList<Workflow<I, O>> workflows;

    @Override
    public ImmutableList getContents() {
        return workflows;
    }

    @Override
    public void handleMutatedList() {
        WorkflowContainer<I, O> nextContainer = (WorkflowContainer<I, O>) callCopyConstructor();
        getParent().getContents().replace(this, nextContainer);
    }

    public AbstractWorkflowContainer() {
        workflows = new ImmutableListImpl<>(this);
    }

    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer) {
        workflows = new ImmutableListImpl<>(oldWorkflowContainer.workflows.getNextList(), this);
    }


}

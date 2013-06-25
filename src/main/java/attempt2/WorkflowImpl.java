package attempt2;

import attempt2.ImmutableCollections.ImmutableList;
import attempt2.ImmutableCollections.ImmutableListImpl;
import lombok.Delegate;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:34
 */
public abstract class WorkflowImpl<I, O> implements Workflow<I, O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    private final ImmutableListImpl<Element<?, ?>> elements;

    @Override
    public ImmutableList<Element<?,?>> getContents() {
        return elements;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMutatedList() {
        Workflow<I, O> nextWorkflow = (Workflow<I, O>) callCopyConstructor();
        for (Element<?, ?> e : elements)
        getParent().getContents().replace(this, nextWorkflow);
    }

    public WorkflowImpl() {
        elements = new ImmutableListImpl<>(this);
    }

    public WorkflowImpl(WorkflowImpl<I, O> oldworkflow) {
        elements = new ImmutableListImpl<>(oldworkflow.elements.getMutatedList(), this);
    }
}

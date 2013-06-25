package BestSoFar.framework.abstractions;

import BestSoFar.ImmutableCollections.ImmutableListImpl;
import BestSoFar.framework.helper.Observable;
import BestSoFar.framework.helper.ObservableImpl;
import lombok.Delegate;
import lombok.Getter;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:34
 */
public abstract class WorkflowImpl<I, O> implements Workflow<I, O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    @Getter private final ImmutableListImpl<Element<?, ?>> elements;


//    @SuppressWarnings("unchecked")
    @Override
    public void handleMutatedList() {
        Workflow<I, O> nextWorkflow = (Workflow<I, O>) callCopyConstructor();

        for (Element<?, ?> e : elements)
            e.setParent(nextWorkflow);

        getParent().getWorkflows().replace(this, nextWorkflow);
    }

    public WorkflowImpl() {
        elements = new ImmutableListImpl<>(this);
    }

    public WorkflowImpl(WorkflowImpl<I, O> oldworkflow) {
        elements = new ImmutableListImpl<>(oldworkflow.elements.getMutatedList(), this);
    }
}

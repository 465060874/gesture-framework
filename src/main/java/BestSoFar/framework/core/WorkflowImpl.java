package BestSoFar.framework.core;

import BestSoFar.ImmutableCollections.ImmutableListImpl;
import BestSoFar.framework.helper.MediatorObserver;
import BestSoFar.framework.helper.Observable;
import BestSoFar.framework.helper.ObservableImpl;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:34
 */
public class WorkflowImpl<I, O> implements Workflow<I, O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    @Getter private final ImmutableListImpl<Element<?, ?>> elements;

    @Getter @Setter @NotNull private WorkflowContainer<I, O> parent;

    @Override
    public void handleMutatedList() {
        Workflow<I, O> nextWorkflow = (Workflow<I, O>) callCopyConstructor();

        for (Element<?, ?> e : elements)
            e.setParent(nextWorkflow);

        // If this returns false, then I have been previously disowned and nothing happens.
        getParent().getWorkflows().replace(this, nextWorkflow);
    }

    public WorkflowImpl(WorkflowContainer<I, O> parent) {
        elements = new ImmutableListImpl<>(this);
        setParent(parent);
    }

    public WorkflowImpl(WorkflowImpl<I, O> oldworkflow) {
        elements = new ImmutableListImpl<>(oldworkflow.elements.getMutatedList(), this);
    }
}

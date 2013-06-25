package BestSoFar.framework.abstractions;

import BestSoFar.ImmutableCollections.ImmutableList;
import BestSoFar.ImmutableCollections.ImmutableListImpl;
import BestSoFar.framework.helper.Observable;
import BestSoFar.framework.helper.ObservableImpl;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:22
 */
public abstract class AbstractWorkflowContainer<I, O> implements WorkflowContainer<I,O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    @Getter private final ImmutableList<Workflow<I, O>> workflows;

    @Getter @Setter @NotNull Workflow<?, ?> parent;

    @Override
    public void handleMutatedList() {
        WorkflowContainer<I, O> nextContainer = (WorkflowContainer<I, O>) callCopyConstructor();
        getParent().getElements().replace(this, nextContainer);
    }

    public AbstractWorkflowContainer() {
        workflows = new ImmutableListImpl<>(this);
    }

    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer) {
        workflows = new ImmutableListImpl<>(oldWorkflowContainer.workflows.getMutatedList(), this);
    }


}

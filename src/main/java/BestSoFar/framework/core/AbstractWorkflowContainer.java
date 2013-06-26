package BestSoFar.framework.core;

import BestSoFar.immutables.ImmutableList;
import BestSoFar.immutables.ImmutableListImpl;
import BestSoFar.framework.helper.ProcessorObserverManager;
import BestSoFar.immutables.TypeData;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:22
 */
public abstract class AbstractWorkflowContainer<I, O> implements WorkflowContainer<I, O> {

    @Delegate private final ProcessorObserverManager<O> observerManager = new ProcessorObserverManager<>();
    @Getter private final ImmutableList<Workflow<I, O>> workflows;
    @Getter @Setter @NotNull Workflow<?, ?> parent;
    private final TypeData<I, O> typeData;

    @Override
    public void handleListMutation() {
        WorkflowContainer<I, O> nextContainer = (WorkflowContainer<I, O>) cloneAs(typeData);
        getParent().getElements().replace(this, nextContainer);
    }

    public AbstractWorkflowContainer(TypeData<I, O> typeData) {
        this.typeData = typeData;
        workflows = new ImmutableListImpl<>(this);
    }

    public AbstractWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer, TypeData<I, O> typeData) {
        this.typeData = typeData;
        workflows = new ImmutableListImpl<>(oldWorkflowContainer.getWorkflows().getMutatedList(), this);
    }


}

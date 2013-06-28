package BestSoFar.framework.core;

import BestSoFar.framework.helper.ChildOf;
import BestSoFar.framework.helper.ParentMutationHandler;
import BestSoFar.framework.helper.ProcessorMutationHandler;
import BestSoFar.immutables.ImmutableList;
import BestSoFar.immutables.ImmutableListImpl;
import BestSoFar.immutables.TypeData;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 10:38
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Delegate private final ChildOf<WorkflowContainer<I, O>> parentManager;
    @Getter private final ImmutableList<Element<?, ?>> elements;
    @Getter @NonNull private final TypeData<I, O> typeData;
    @Delegate private final ProcessorMutationHandler<I, O, I, O> mutationHandler =
            new ProcessorMutationHandler<>(this);

    public AbstractWorkflow(WorkflowContainer<I, O> parent, TypeData<I, O> typeData) {
        elements = new ImmutableListImpl<>(this);
        this.typeData = typeData;
        parentManager = new ParentMutationHandler<>(parent, this);
        checkTypeData();
    }


    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        elements = oldWorkflow.elements.makeReplacementFor(this);
        parentManager = ((ParentMutationHandler<WorkflowContainer<I,O>>) oldWorkflow.parentManager)
                            .makeReplacementFor(this);

        checkTypeData();
    }

    private void checkTypeData() {
        if (!typeData.equals(getParent().getTypeData())) {
            String msg = String.format(
                    "Workflow%s must have same type data as WorkflowContainer%s",
                    typeData.toString(),
                    getParent().getTypeData().toString()
            );
            throw new ClassCastException(msg);
        }
    }

    @Override
    public <I2, O2> void replaceSelfWithClone(Processor<I2, O2> clone) {
        Workflow<I, O> nextWorkflow = (Workflow<I, O>) clone;

        for (Element<?, ?> e : elements)
            e.setParent(nextWorkflow);

        // If this returns false, then I have been previously disowned and nothing happens.
        getParent().getWorkflows().replace(this, nextWorkflow);
    }
}

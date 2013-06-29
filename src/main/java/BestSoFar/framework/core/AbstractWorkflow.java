package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.immutables.ImmutableList;
import BestSoFar.framework.core.helper.ParentBox;
import BestSoFar.framework.core.helper.ProcessorMutationHandler;
import BestSoFar.framework.core.helper.TypeData;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

/**
 * Abstract implementation of Workflow.  Manages parent, the list of elements in the workflow,
 * typedata, and mutation management.
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Delegate private final ChildOf<WorkflowContainer<I, O>> parentManager;
    @Getter private final ImmutableList<Element<?, ?>> elements;
    @Getter @NonNull private final TypeData<I, O> typeData;
    @Delegate private final ProcessorMutationHandler<I, O, I, O> mutationHandler =
            new ProcessorMutationHandler<>(this);

    public AbstractWorkflow(WorkflowContainer<I, O> parent, TypeData<I, O> typeData) {
        elements = new ImmutableList<>(this);
        this.typeData = typeData;
        parentManager = new ParentBox<>(parent, this);
        checkTypeData();
    }


    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        elements = oldWorkflow.elements.assignReplacementTo(this);
        parentManager = ((ParentBox<WorkflowContainer<I,O>>) oldWorkflow.parentManager)
                            .assignReplacementTo(this);

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

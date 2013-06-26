package BestSoFar.framework.core;

import BestSoFar.framework.helper.*;
import BestSoFar.immutables.ImmutableListImpl;
import BestSoFar.immutables.TypeData;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;

import java.util.*;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl<I, O> implements Workflow<I, O> {
    @Delegate private final ChildOf<WorkflowContainer<I, O>> parentManager;
    @Getter private final ImmutableListImpl<Element<?, ?>> elements;
    @Getter @NotNull private final TypeData<I, O> typeData;
    @Delegate private final ProcessorMutationHandler<I, O, I, O> mutationHandler =
            new ProcessorMutationHandler<>(this);

    public WorkflowImpl(WorkflowContainer<I, O> parent, TypeData<I, O> typeData) {
        elements = new ImmutableListImpl<>(this);
        this.typeData = typeData;
        parentManager = new ParentMutationHandler<>(parent, this);
        checkTypeData();
    }

    @SuppressWarnings("unchecked")
    public WorkflowImpl(WorkflowImpl<?, ?> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        elements = new ImmutableListImpl<>(oldWorkflow.getElements().getMutatedList(), this);
        parentManager = ((ParentMutationHandler<WorkflowContainer<I,O>>) oldWorkflow.parentManager).cloneFor(this);
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

    @Override
    public boolean isValid() {
        if (elements.size() == 0) {
            return typeData.canBeEmptyContainer();
        } else {

            if ( !elements.get(1).getTypeData().canBeAtStartOfContainer(typeData) ||
                    !elements.get(elements.size()-1).getTypeData().canBeAtEndOfContainer(typeData) )
                return false;

            TypeData<?, ?> previousType = null;

            for (Element<?,?> e : elements) {
                if (previousType != null)
                    if ( !e.getTypeData().canComeAfter(previousType) )
                        return false;

                previousType = e.getTypeData();
            }

            return true;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mediator<O> process(Mediator<?> input) {
        for (Element<?,?> e : elements)
            input = e.process(input);

        Mediator<O> output = (Mediator<O>) input;

        return output;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : elements)
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        List<Mediator<O>> outputs = (List<Mediator<O>>) inputs;

        return outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        ListIterator<Element<?, ?>> itr = elements.listIterator(elements.size());
        Map<Mediator<?>, Mediator<?>> backwardMapping;
        Map<Mediator<?>, Mediator<?>> totalBackwardMapping = new HashMap<>();

        for (Mediator<?> m : completedOutputs)
            totalBackwardMapping.put(m, m);

        List<Mediator<?>> completedInputs;
        Set<Mediator<?>> successfulInputs;

        while(itr.hasPrevious()) {
            backwardMapping = (Map<Mediator<?>, Mediator<?>>)
                    itr.previous().createBackwardMappingForTrainingBatch(completedOutputs, successfulOutputs).entrySet();

            completedInputs = new LinkedList<>();
            successfulInputs = new HashSet<>();

            Mediator.mapMediatorsBackward(completedOutputs, backwardMapping, completedInputs);
            Mediator.mapMediatorsBackward(successfulOutputs, backwardMapping, successfulInputs);

            completedOutputs = completedInputs;
            successfulOutputs = successfulInputs;

            for (Map.Entry<Mediator<?>, Mediator<?>> entry : totalBackwardMapping.entrySet()) {
                Mediator<?> input = entry.getValue();
                Mediator<?> earlierInput = backwardMapping.get(input);
                entry.setValue(earlierInput);
            }
        }

        return (Map<Mediator<O>, Mediator<I>>) totalBackwardMapping;
    }

    @Override
    public <I2, O2> WorkflowImpl<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
        return new WorkflowImpl<>(this, typeData);
    }
}

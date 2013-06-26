package BestSoFar.framework.core;

import BestSoFar.immutables.ImmutableListImpl;
import BestSoFar.framework.helper.Mediator;
import BestSoFar.framework.helper.ProcessorObserverManager;
import BestSoFar.immutables.TypeData;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;

import java.util.*;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl<I, O> implements Workflow<I, O> {

    @Delegate private final ProcessorObserverManager<O> observerManager = new ProcessorObserverManager<>();
    @Getter private final ImmutableListImpl<Element<?, ?>> elements;
    @Getter @NotNull private WorkflowContainer<I, O> parent;
    @Getter @NotNull private final TypeData<I, O> typeData;

    public WorkflowImpl(WorkflowContainer<I, O> parent, TypeData<I, O> typeData) {
        elements = new ImmutableListImpl<>(this);
        setParent(parent);
        this.typeData = typeData;
        checkTypeData();
    }

    public WorkflowImpl(WorkflowImpl<?, ?> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        elements = new ImmutableListImpl<>(oldWorkflow.getElements().getMutatedList(), this);
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
    public void setParent(WorkflowContainer<I, O> parent) {
        this.parent = parent;
        checkTypeData();
    }

    @Override
    public void handleListMutation() {
        Workflow<I, O> nextWorkflow = (Workflow<I, O>) cloneAs(typeData);

        for (Element<?, ?> e : elements)
            e.setParent(nextWorkflow);

        // If this returns false, then I have been previously disowned and nothing happens.
        getParent().getWorkflows().replace(this, nextWorkflow);
    }

    @Override
    public boolean isValid() {
        if (elements.size() == 0) {
            return typeData.getInputType() == typeData.getOutputType();
        } else {
            Class<?> inType, outType = typeData.getInputType();

            for (Element<?,?> e : elements) {
                inType = e.getTypeData().getInputType();

                if (inType != outType)
                    return false;

                outType = e.getTypeData().getOutputType();
            }

            return outType == typeData.getOutputType();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mediator<O> process(Mediator<?> input) {
        for (Element<?,?> e : elements)
            input = e.process(input);



        return (Mediator<O>) input;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : elements)
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        return (List<Mediator<O>>) inputs;
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
    public <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
        return new WorkflowImpl<>(this, typeData);
    }
}

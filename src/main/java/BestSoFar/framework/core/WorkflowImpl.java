package BestSoFar.framework.core;

import BestSoFar.framework.helper.Mediator;
import BestSoFar.framework.helper.TypeData;

import java.util.*;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl<I, O> extends AbstractWorkflow<I, O> {


    public WorkflowImpl(WorkflowContainer<I, O> parent, TypeData<I, O> typeData) {
        super(parent, typeData);
    }


    public WorkflowImpl(WorkflowImpl<I, O> oldWorkflow, TypeData<I, O> typeData) {
        super(oldWorkflow, typeData);
    }


    @Override
    public boolean isValid() {
        if (getElements().size() == 0) {
            return getTypeData().canBeEmptyContainer();
        } else {

            if ( !getElements().get(1).getTypeData().canBeAtStartOfContainer(getTypeData()) ||
                    !getElements().get(getElements().size()-1).getTypeData().canBeAtEndOfContainer
                            (getTypeData()
                    ) )
                return false;

            TypeData<?, ?> previousType = null;

            for (Element<?,?> e : getElements()) {
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
        for (Element<?,?> e : getElements())
            input = e.process(input);

        Mediator<O> output = (Mediator<O>) input;

        return output;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : getElements())
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        List<Mediator<O>> outputs = (List<Mediator<O>>) (List<?>) inputs;

        return outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        ListIterator<Element<?, ?>> itr = getElements().listIterator(getElements().size());
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

        return (Map<Mediator<O>, Mediator<I>>) (Map<?,?>) totalBackwardMapping;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I2, O2> WorkflowImpl<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
        return new WorkflowImpl<>((WorkflowImpl<I2, O2>) this, typeData);
    }
}

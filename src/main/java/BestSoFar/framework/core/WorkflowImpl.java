package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.common.HandledImmutable;

import java.util.*;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl<I, O> extends AbstractWorkflow<I, O> {


    public WorkflowImpl(TypeData<I, O> typeData, boolean mutable) {
        super(typeData, mutable);
    }


    public WorkflowImpl(WorkflowImpl<I, O> oldWorkflow,
                        TypeData<I, O> typeData, boolean mutable) {
        super(oldWorkflow, typeData, mutable);
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

        return (Mediator<O>) input;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : getElements())
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        return (List<Mediator<O>>) (List<?>) inputs;
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

    @Override
    public WorkflowImpl<I, O> createClone(boolean mutable) {
        return new WorkflowImpl<>(this, getTypeData(), mutable);
    }
}

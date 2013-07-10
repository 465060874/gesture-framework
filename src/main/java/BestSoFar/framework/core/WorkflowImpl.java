package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.core.helper.TypeData;

import java.util.*;

/**
 * Implementation of Workflow.
 */
final public class WorkflowImpl<I, O> extends AbstractWorkflow<I, O> {


    public WorkflowImpl(TypeData<I, O> typeData) {
        super(typeData);
    }


    private WorkflowImpl(WorkflowImpl<I, O> oldWorkflow,
                        TypeData<I, O> typeData) {
        super(oldWorkflow, typeData);
    }


    @Override
    public boolean isValid() {
        if (getChildren().size() == 0) {
            return getTypeData().canBeEmptyContainer();
        } else {

            if ( !getChildren().get(1).getTypeData().canBeAtStartOfWorkflow(getTypeData()) ||
                    !getChildren().get(getChildren().size()-1).getTypeData().canBeAtEndOfWorkflow
                            (getTypeData()
                            ) )
                return false;

            TypeData<?, ?> previousType = null;

            for (Element<?,?> e : getChildren()) {
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
        for (Element<?,?> e : getChildren())
            input = e.process(input);

        return (Mediator<O>) input;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : getChildren())
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        return (List<Mediator<O>>) (List<?>) inputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        ListIterator<Element<?, ?>> itr = getChildren().listIterator(getChildren().size());
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
    public WorkflowImpl<I, O> createMutableClone() {
        return new WorkflowImpl<>(this, getTypeData());
    }
}

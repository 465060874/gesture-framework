package BestSoFar.framework.core;

import BestSoFar.framework.helper.Mediator;
import BestSoFar.immutables.TypeData;

import java.util.List;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 20:21
 */
public class TopLevelWorkflowContainer<I, O> extends AbstractWorkflowContainer<I, O> {
    public TopLevelWorkflowContainer(Workflow<?, ?> parent, TypeData<I, O> typeData) {
        super(parent, typeData);
    }

    public TopLevelWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer, TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
    }

    @Override
    public Mediator<O> process(Mediator<?> input) {
        return null; // Dummy implementation
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        return null; // Dummy implementation
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
        return new TopLevelWorkflowContainer<I2, O2>((AbstractWorkflowContainer<I2,O2>) this, typeData);
    }
}

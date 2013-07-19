package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 09:21
 */
public class TopWorkflowContainer<I, O> extends AbstractWorkflowContainer<I, O> {

    public TopWorkflowContainer(TypeData<I, O> typeData) {
        super(typeData);
    }

    public TopWorkflowContainer(TopWorkflowContainer<?, ?> oldWorkflowContainer,
                                TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
    }

    @Override
    public Mediator<O> process(Mediator<?> input) {
        return null; // Dummy implementation
    }

    @Override
    public <I2, O2> TopWorkflowContainer<I2, O2> withTypeData(TypeData<I2, O2> newTypeData) {
        return new TopWorkflowContainer<>(this, newTypeData);
    }
}

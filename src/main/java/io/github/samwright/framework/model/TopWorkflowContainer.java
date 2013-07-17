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

    public TopWorkflowContainer(AbstractWorkflowContainer<I, O> oldWorkflowContainer, TypeData<I, O> typeData) {
        super(oldWorkflowContainer, typeData);
    }

    @Override
    public AbstractWorkflowContainer<I, O> createMutableClone() {
        return new TopWorkflowContainer<>(this, getTypeData());
    }

    @Override
    public Mediator<O> process(Mediator<?> input) {
        return null; // Dummy implementation
    }
}

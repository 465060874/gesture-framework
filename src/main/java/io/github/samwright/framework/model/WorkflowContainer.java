package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ParentOf;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;

/**
 * A {@code WorkflowContainer} is an {@link Element} that contains one or more {@link Workflow}
 * objects.
 * <p/>
 * When a {@code WorkflowContainer} is asked to process a {@link Mediator},
 * it must choose one of its {@link Workflow} children to perform the processing,
 * so only one {@code Mediator} is returned.
 * <p/>
 * When asked to process a training batch, the {@code WorkflowContainer} must process each input
 * {@code Mediator} with every {@code Workflow} that could conceivably process it,
 * creating an output {@code Mediator} for each {@code Workflow} and for each input
 * {@code Mediator}.
 */
public interface WorkflowContainer<I, O> extends Element<I, O>, ParentOf<Workflow<I, O>> {

    @Override
    WorkflowContainer<I, O> withChildren(List<Workflow<I, O>> newChildren);

    @Override
    WorkflowContainer<I, O> withParent(Workflow<?, ?> newParent);

    @Override
    <I2, O2> WorkflowContainer<I2, O2> withTypeData(TypeData<I2, O2> newTypeData);
}

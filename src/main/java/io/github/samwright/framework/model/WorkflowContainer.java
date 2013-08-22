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
public interface WorkflowContainer extends Element, ParentOf<Workflow> {

    @Override
    WorkflowContainer withChildren(List<Workflow> newChildren);

    @Override
    WorkflowContainer withParent(Workflow newParent);

    @Override
    WorkflowContainer withTypeData(TypeData newTypeData);

    @Override
    WorkflowContainer createMutableClone();

    @Override
    WorkflowContainer createOrphanedDeepClone();
}

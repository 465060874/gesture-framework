package BestSoFar.framework.core;

import BestSoFar.immutables.ImmutableList;

/**
 * A WorkflowContainer is an Element that contains one or more Workflows.
 *
 * When a WorkflowContainer is asked to process a mediator, it must choose one of its workflows and have it perform
 * the processing, so only one mediator is returned.
 *
 * Before being asked to process a mediator, it will first process a training batch.  A typical sequence that the
 * WorkflowContainer methods are called is:
 *      - notify(List<Mediator<O>>) : from observed Processors as they process the training batch
 *      - processTrainingBatch(..)  : when it's this object's turn to process the training batch
 *      - createBackwardMappingForTrainingBatch(..) : when the completed training batch is travelling backward over
 *                                                    the workflow that created it
 */
public interface WorkflowContainer<I, O> extends Element<I, O> {

    ImmutableList<Workflow<I, O>> getWorkflows();
}

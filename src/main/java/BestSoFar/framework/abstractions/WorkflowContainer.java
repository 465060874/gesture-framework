package BestSoFar.framework.abstractions;

import BestSoFar.ImmutableCollections.ImmutableList;
import BestSoFar.ImmutableCollections.ImmutableListHandler;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 08:43
 */
public interface WorkflowContainer<I, O> extends Element<I, O>, ImmutableListHandler {

    ImmutableList<Workflow<I, O>> getWorkflows();
}

package attempt2;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 08:43
 */
public interface WorkflowContainer<I, O> extends Element<I, O>, ImmutableListHandler<Workflow<I, O>> {
}

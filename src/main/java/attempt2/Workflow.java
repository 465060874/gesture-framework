package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 12:55
 */
public interface Workflow<I, O>
        extends Processor<I, O>, ImmutableList<Element<?, ?>>, SingleParentChild<WorkflowContainer<I, O>> {



}




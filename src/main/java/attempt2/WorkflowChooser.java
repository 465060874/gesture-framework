package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 13:02
 */
public interface WorkflowChooser<I, O>
        extends Processor<I, O>, ImmutableList<Workflow<I, O>> {



}

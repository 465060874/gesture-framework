package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 12:55
 */
public interface Workflow<I, O> extends Processor<I, O>, ImmutableListHandler<Element<?, ?>> {

    /**
     * The workflow container in which this workflow resides.
     *
     * The parent field is always up to date - eg. if the parent is replaced, this returns
     * the new parent.  The 'process' flow of data already in the system is unchanged (ie.
     * still flows back to the old parent) since everything is immutable.
     *
     * @return the workflow container in which this workflow resides.
     */
    WorkflowContainer<I, O> getParent();

}




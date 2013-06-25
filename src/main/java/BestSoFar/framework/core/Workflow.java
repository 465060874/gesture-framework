package BestSoFar.framework.core;

import BestSoFar.ImmutableCollections.ImmutableList;
import BestSoFar.ImmutableCollections.ImmutableListHandler;

/**
 * A workflow is a linear list of Element objects.  When a workflow processes an input,
 * it has its Element objects process the data sequentially (ie. * input -> Element1 -> data1 -> Element2 -> data2 ->
 * ... -> LastElement -> output).
 *
 * The workflow's input and output types are independent of its elements, and are set at the workflow's construction.
 * It is perfectly legal to have the wrong input/output types for the workflow, or to have neighbouring elements be
 * incompatible. In these situations, the workflow will return 'false' for 'isValid()',
 * and should have a way to inform the user (eg. in the associated view there might be connectors between
 * neighbouring elements which are red if incompatible).
 *
 * Workflows (like all Processors) are strictly immutable, so "mutating" them (by changing their list of elements) in
 * fact creates a copy with desired changes.  The old workflow must replace itself in its parent WorkflowContainer's
 * workflow list with this new version, and inform its elements of their new parent.
 *
 * As such, the parent field is always up to date - eg. if the parent is replaced (because it was modified) then this
 * object's parent field is updated (even though data returned by the 'process' method will still flow back to the
 * original parent).  This means that data flows through the system as it was when the data was entered into it.
 *
 * The only exception is when a parent disowns this object, in which case the parent field can be the old parent.
 * This is allowed because the parent should be the only object with a pointer to this object,
 * so nothing will access it.
 */
public interface Workflow<I, O> extends Processor<I, O>, ImmutableListHandler {

    /**
     * Returns the WorkflowContainer in which this workflow resides.
     *
     * @return the workflow container in which this workflow resides.
     */
    WorkflowContainer<I, O> getParent();

    /**
     * Sets the WorkflowContainer in which this workflow resides
     *
     * @param parent the new parent.
     */
    void setParent(WorkflowContainer<I, O> parent);

    /**
     * Returns the immutable list of elements inside this workflow.  Attempting to mutate this list results in a new
     * Workflow being created and integrated into the new system.
     *
     * @return the list of elements inside this workflow.
     */
    ImmutableList<Element<?, ?>> getElements();

}




package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.ParentOf;

import java.util.List;

/**
 * A workflow is a linear list of {@link Element} objects which sits in a
 * {@link WorkflowContainer}.
 * <p/>
 * When a workflow processes an input, it has its {@code Element} objects process the data
 * sequentially, for example:
 * {@code input -> lement1 -> data1 -> Element2 -> data2 -> ... -> LastElement -> output}.
 * <p/>
 * The {@code Workflow's} input and output types are independent of its {@code Elements},
 * and are set at the {@code workflow's} construction.
 * <p/>
 * It is perfectly legal to have the wrong input/output types for the {@code workflow},
 * or to have neighbouring elements be type-incompatible. In these situations,
 * the workflow will return {@code false} for {@code isValid()}, and should have a way to inform
 * the user (eg. in the associated view there might be connectors between neighbouring elements
 * which are red if incompatible).
 * <p/>
 * NB. For two elements to be type-compatible, the previous {@code Element's} output type must be
 * castable to the next {@code Element's} input type.
 * <p/>
 * However, a {@code Workflow} MUST be type-compatible with its parent {@code WorkflowContainer}.
 * This is done so that all code related to checking neighbouring elements' data types is in
 * {@code Workflow} (and nowhere else).  Otherwise all {@code WorkflowContainers} would have to
 * worry about whether its {@code Workflows} match it's data types.  If this assertion is ever
 * broken, a {@code ClassCastException} is thrown.
 */
public interface Workflow extends Processor, ChildOf<WorkflowContainer>, ParentOf<Element> {

    /**
     * Returns the subset of this workflow's children which are invalid because their output type
     * doesn't match the next element's input type.  If this workflow's input type doesn't match
     * the first element's input type, the list will also contain a {@code null}.
     *
     * @return the child elements that are invalid due to their order in this workflow.
     */
    List<Element> getInvalidlyOrderedElements();

    @Override
    Workflow withChildren(List<Element> newChildren);

    @Override
    Workflow withParent(WorkflowContainer newParent);

    @Override
    Workflow createMutableClone();

    @Override
    Workflow getCurrentVersion();
}




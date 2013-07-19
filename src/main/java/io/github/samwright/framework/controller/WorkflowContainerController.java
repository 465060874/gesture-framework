package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowContainer;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 20:14
 */
abstract public class WorkflowContainerController<I, O> extends ElementController<I, O> {

    public WorkflowContainerController(String fxmlResource) {
        super(fxmlResource);
    }

    @Override
    public WorkflowContainer<I, O> getModel() {
        return (WorkflowContainer<I, O>) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        for (Workflow<I, O> workflow : getModel().getChildren())
            workflow.getController().handleUpdatedModel();
    }
}

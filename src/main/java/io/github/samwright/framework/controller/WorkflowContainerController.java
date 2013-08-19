package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowContainer;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 20:14
 */
abstract public class WorkflowContainerController extends ElementController {

    public WorkflowContainerController(String fxmlResource) {
        super(fxmlResource);
    }

    @Override
    public WorkflowContainer getModel() {
        return (WorkflowContainer) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {

        for (Workflow workflow : getModel().getChildren())
            workflow.getController().handleUpdatedModel();
    }
}

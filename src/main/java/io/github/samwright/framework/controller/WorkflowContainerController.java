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

    public WorkflowContainerController(WorkflowContainerController toClone) {
        super(toClone);
    }

    @Override
    public WorkflowContainer getModel() {
        return (WorkflowContainer) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        for (Workflow workflow : getModel().getChildren())
            workflow.getController().handleUpdatedModel();
    }

    @Override
    abstract public WorkflowContainerController createClone();

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);

        for (Workflow workflow : getModel().getCurrentVersion().getChildren())
            ((WorkflowController) workflow.getController()).setBeingDragged(beingDragged);
    }
}

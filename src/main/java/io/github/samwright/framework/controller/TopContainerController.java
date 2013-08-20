package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Workflow;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 18:05
 */
public class TopContainerController extends WorkflowController {
    public TopContainerController() {
        super("fxml/WorkflowContainer.fxml");
    }

    @Override
    public ModelController<Workflow> createClone() {
        return new TopContainerController();
    }

    public TopContainerController(WorkflowController toClone) {
        super(toClone);
    }
}

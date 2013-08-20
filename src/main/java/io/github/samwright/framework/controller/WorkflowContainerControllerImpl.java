package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:51
 */
public class WorkflowContainerControllerImpl extends WorkflowContainerController {

    @FXML
    private VBox workflowContainer;

    @FXML
    private VBox workflowsBox;

    private Workflow spareWorkflow;

    public WorkflowContainerControllerImpl(String fxmlResource) {
        super(fxmlResource);
    }

    public WorkflowContainerControllerImpl(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public ModelController<Element> createClone() {
        return new WorkflowContainerControllerImpl(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        workflowsBox.getChildren().clear();
        for (Workflow workflow : getModel().getChildren())
            workflowsBox.getChildren().add(workflow.getController());
    }
}

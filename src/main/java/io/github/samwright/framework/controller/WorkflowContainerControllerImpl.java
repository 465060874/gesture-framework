package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Workflow;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:51
 */
public class WorkflowContainerControllerImpl<I, O> extends WorkflowContainerController<I, O> {

    @FXML
    private VBox workflowContainer;

    @FXML
    private VBox workflowsBox;

    private Workflow<I, O> spareWorkflow;

    public WorkflowContainerControllerImpl(String fxmlResource) {
//        super("/fxml/WorkflowContainer.fxml", model);
        super(fxmlResource);


    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        workflowsBox.getChildren().clear();
        for (Workflow<I, O> workflow : getModel().getChildren())
            workflowsBox.getChildren().add(workflow.getController());
    }
}

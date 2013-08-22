package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowContainer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:51
 */
public class WorkflowContainerControllerImpl extends WorkflowContainerController {

    @FXML
    protected Button addButton;

    @FXML
    protected VBox workflowsBox;

    {
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addNewWorkflow();
            }
        });
    }

    public WorkflowContainerControllerImpl() {
        super("/fxml/WorkflowContainer.fxml");
    }

    public WorkflowContainerControllerImpl(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public ModelController<Element> createClone() {
        return new WorkflowContainerControllerImpl(this);
    }



    public void addNewWorkflow() {
        List<Workflow> newChildren = new ArrayList<>(getModel().getChildren());
        newChildren.add(new WorkflowControllerImpl().getModel());
        WorkflowContainer newModel = getModel().withChildren(newChildren);
        getModel().replaceWith(newModel);
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

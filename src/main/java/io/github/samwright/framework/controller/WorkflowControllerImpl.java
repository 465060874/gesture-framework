package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:55
 */
public class WorkflowControllerImpl extends WorkflowController {

    @FXML
    private VBox workflow;

    @FXML
    private HBox elementsBox;

    public WorkflowControllerImpl() {
        super("/fxml/Workflow.fxml");
    }

    public WorkflowControllerImpl(WorkflowControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public ModelController<Workflow> createClone() {
        return new WorkflowControllerImpl(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        elementsBox.getChildren().clear();

        elementsBox.getChildren().add(new ElementLink());

        for (Element element : getModel().getChildren()) {
            elementsBox.getChildren().add(element.getController());
            elementsBox.getChildren().add(new ElementLink());
        }
    }
}

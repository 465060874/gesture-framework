package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.WorkflowLinkController;
import io.github.samwright.framework.model.Element;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:55
 */
public class WorkflowControllerImpl<I, O> extends WorkflowController<I, O> {

    @FXML
    private VBox workflow;

    @FXML
    private HBox elementsBox;

    public WorkflowControllerImpl(String fxmlResource) {
        super(fxmlResource);
        System.out.println("fxml = " + fxmlResource);

        System.out.println("workflow = " + workflow);
        System.out.println("elementsBox = " + elementsBox);
    }


    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        System.out.println("workflow-controller:1");
        elementsBox.getChildren().clear();
        System.out.println("workflow-controller:2");
        elementsBox.getChildren().add(new WorkflowLinkController());
        System.out.println("workflow-controller:3");

        for (Element<?, ?> element : getModel().getChildren()) {
            elementsBox.getChildren().add(element.getController());
            System.out.println("workflow-controller:4");
            elementsBox.getChildren().add(new WorkflowLinkController());
            System.out.println("workflow-controller:5");
        }
    }
}

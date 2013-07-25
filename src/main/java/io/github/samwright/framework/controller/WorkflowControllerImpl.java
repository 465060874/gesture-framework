package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.model.Element;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        elementsBox.getChildren().clear();

        elementsBox.getChildren().add((Node) MainApp.beanFactory.getBean("elementLink"));

        for (Element<?, ?> element : getModel().getChildren()) {
            elementsBox.getChildren().add(element.getController());
            elementsBox.getChildren().add((Node) MainApp.beanFactory.getBean("elementLink"));
        }
    }
}

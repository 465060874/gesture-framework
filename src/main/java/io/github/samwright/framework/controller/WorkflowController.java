package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.WorkflowLinkController;
import io.github.samwright.framework.model.common.Replaceable;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 21:15
 */
public class WorkflowController extends AbstractModelController {

    @FXML
    private HBox elementsBox;

    public WorkflowController(@NonNull Replaceable model) {
        super("/fxml/Workflow.fxml", model);
    }

    @Override
    public void notify(Replaceable model) {
        super.notify(model);
        elementsBox.getChildren().add(new WorkflowLinkController());
    }
}

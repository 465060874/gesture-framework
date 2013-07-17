package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowImpl;
import io.github.samwright.framework.model.common.Replaceable;
import io.github.samwright.framework.model.helper.TypeData;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 20:14
 */
public class WorkflowContainerController extends AbstractModelController {

    @FXML
    private VBox workflowsBox;

    public WorkflowContainerController(@NonNull Replaceable model) {
        super("/fxml/WorkflowContainer.fxml", model);
        System.out.println("workflowsBox = " + workflowsBox);

        Workflow w1 = new WorkflowImpl<>(new TypeData<>(Integer.class, Integer.class));
        Workflow w2 = new WorkflowImpl<>(new TypeData<>(Integer.class, Integer.class));

        workflowsBox.getChildren().add(new WorkflowController(w1));
        workflowsBox.getChildren().add(new WorkflowController(w2));

    }
}

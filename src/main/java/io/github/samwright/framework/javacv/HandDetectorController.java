package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.WorkflowContainerControllerImpl;
import io.github.samwright.framework.controller.WorkflowControllerImpl;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.WorkflowContainer;

import java.util.Arrays;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 15:06
 */
public class HandDetectorController extends WorkflowContainerControllerImpl {

    {
        header.getChildren().remove(addButton);
        containerLabel.setText("Hand Detector");
    }

    public HandDetectorController() {
        WorkflowContainer model = new HandDetector();

        model = model.withChildren(Arrays.asList(
                new WorkflowControllerImpl().getModel(),
                new WorkflowControllerImpl().getModel(),
                new WorkflowControllerImpl().getModel()
        ));

        model.replace(null);
        proposeModel(model);
        setElementLink(new ElementLink());
    }

    public HandDetectorController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    private void relabelWorkflows() {
        relabelWorkflow(0, "Contour");
        relabelWorkflow(1, "Palm");
        relabelWorkflow(2, "Fingertips");
    }

    private void relabelWorkflow(int index, String label) {
        WorkflowControllerImpl workflowController
                = (WorkflowControllerImpl) workflowsBox.getChildren().get(index);
        workflowController.setLabel(label);
    }

    @Override
    public HandDetectorController createClone() {
        return new HandDetectorController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        relabelWorkflows();
    }
}

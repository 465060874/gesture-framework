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

        model = model.withChildren(Arrays.asList(new WorkflowControllerImpl().getModel(),
                new WorkflowControllerImpl().getModel()));
        model.replace(null);
        proposeModel(model);
        setElementLink(new ElementLink());
    }

    public HandDetectorController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    private void relabelWorkflows() {
        WorkflowControllerImpl contourWorkflow = (WorkflowControllerImpl) workflowsBox
                .getChildren().get(0);
        contourWorkflow.setLabel("Contour");

        WorkflowControllerImpl convexHullWorkflow = (WorkflowControllerImpl) workflowsBox
                .getChildren().get(1);
        convexHullWorkflow.setLabel("Convex Hull");
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

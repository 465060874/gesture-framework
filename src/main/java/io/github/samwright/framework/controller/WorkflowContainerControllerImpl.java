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
        System.out.println("workflow container controller:1");

        workflowsBox.getChildren().clear();
        System.out.println("workflow container controller:2");
        for (Workflow<I, O> workflow : getModel().getChildren()) {
            System.out.println("workflow container controller:3 (workflow = " + workflow +
                    " , controller = " + workflow.getController());
            workflowsBox.getChildren().add(workflow.getController());
            System.out.println("workflow container controller:4");
        }


//        spareWorkflow = new WorkflowImpl<>(getModel().getTypeData());

//
//        spareWorkflow = (Workflow<I, O>) MainApp.beanFactory.getBean("workflow");
//        System.out.println("workflow container controller:1b");
//        spareWorkflow = spareWorkflow.withTypeData(getModel().getTypeData());
//
//
//        System.out.println("workflow container controller:2");
//        ModelController controller = spareWorkflow.getController();
//        System.out.println("workflow container controller:3 (controller = " + controller);
//        workflowsBox.getChildren().add(controller);
//        System.out.println("workflow container controller:4");
    }
}

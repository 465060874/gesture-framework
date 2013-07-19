package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.Workflow;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.util.Arrays;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:55
 */
public class MainWindowController extends HBox {

    @FXML
    private ScrollPane mainScrollPanel;

    private static TopWorkflowContainer topModel;
    @Getter private static ModelController topController;

    @SuppressWarnings("unchecked")
    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        topModel = (TopWorkflowContainer) MainApp.beanFactory.getBean("topWorkflowContainer");
        mainScrollPanel.setContent(topModel.getController());
        topController = topModel.getController();

        Workflow spareWorkflow = (Workflow) MainApp.beanFactory.getBean("workflow");

        System.out.println("main controller:1b (controller = " + spareWorkflow.getController());
        spareWorkflow = spareWorkflow.withTypeData(topModel.getTypeData());
        System.out.println("main controller:2 (controller = " + spareWorkflow.getController());

        TopWorkflowContainer oldTopModel = topModel;
        topModel = (TopWorkflowContainer) topModel.withChildren(Arrays.asList(spareWorkflow));
        System.out.println("main controller:3");
        oldTopModel.replaceWith(topModel);
        System.out.println("main controller:4");
//        ModelController controller = spareWorkflow.getController();
//        System.out.println("workflow container controller:3 (controller = " + controller);
//        workflowsBox.getChildren().add(controller);
//        System.out.println("workflow container controller:4");
    }
}

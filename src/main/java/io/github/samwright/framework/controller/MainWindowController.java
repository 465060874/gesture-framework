package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.Workflow;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import lombok.Getter;

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

        Workflow emptyWorkflow = (Workflow) MainApp.beanFactory.getBean("workflow");

        Workflow spareWorkflow = emptyWorkflow.withTypeData(topModel.getTypeData()).withParent(topModel);
        emptyWorkflow.replaceWith(spareWorkflow);
    }
}

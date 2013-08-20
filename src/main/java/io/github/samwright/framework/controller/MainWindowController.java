package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.TopWorkflowContainer;
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
    private static ToolboxController toolboxController;

    @SuppressWarnings("unchecked")
    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        topModel = new TopWorkflowContainer();
        mainScrollPanel.setContent(topModel.getController());
        topController = topModel.getController();

        toolboxController = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        getChildren().add(toolboxController);

        toolboxController.getChildren().add(new LabelledElementController("Element1"));
        toolboxController.getChildren().add(new LabelledElementController("Element2"));
        toolboxController.getChildren().add(new LabelledElementController("Element3"));
    }

}


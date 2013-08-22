package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.example.ExampleController;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.WorkflowContainer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:55
 */
public class MainWindowController extends VBox {

    @FXML
    private ScrollPane mainScrollPanel;

    @FXML
    private HBox hBox;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    private static WorkflowContainer topModel;
    @Getter private static TopContainerController topController;
    private static ToolboxController toolboxController;

    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        topController = new TopContainerController(this);
        mainScrollPanel.setContent(topController);
        topModel = topController.getModel();

        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                topController.undo();
            }
        });

        redoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                topController.redo();
            }
        });

        toolboxController = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        hBox.getChildren().add(toolboxController);

        toolboxController.getChildren().add(new ExampleController("Element1"));
        toolboxController.getChildren().add(new ExampleController("Element2"));
        toolboxController.getChildren().add(new ExampleController("Element3"));
    }

    public void handleUpdatedModel() {
        redoButton.setDisable(!topController.canRedo());
        undoButton.setDisable(!topController.canUndo());
    }


}


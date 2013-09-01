package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.example.ExElementController;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.helper.ModelLoader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    @Getter private static TopContainerController topController;
    private static ToolboxController toolboxController;

    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        topController = new TopContainerController(this);
        mainScrollPanel.setContent(topController);

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
        toolboxController.setStyle("-fx-vgap: 5;-fx-hgap: 5;-fx-padding: 5");
        hBox.getChildren().add(toolboxController);

        topController.handleUpdatedModel();
//        topController.addNewWorkflow();
        ModelLoader.registerPrototypeModel(topController.getModel());
        setOnKeyPressed(topController.getKeyPressHandler());

        ModelLoader.registerPrototypeModel(new ExElementController("Element1").getModel());
        ModelLoader.registerPrototypeModel(new ExElementController("Element2").getModel());
        ModelLoader.registerPrototypeModel(new ExElementController("Element3").getModel());
//        ModelLoader.registerPrototypeModel(new ExContainerController().getModel());
        ModelLoader.registerPrototypeModel(new WorkflowControllerImpl().getModel());

        for (Processor p : ModelLoader.getAllProtoypeModels())
            if (p instanceof Element && !(p instanceof TopWorkflowContainer))
                toolboxController.getChildren().add((Node) p.getController());
    }

    public void handleUpdatedModel() {
        redoButton.setDisable(!topController.canRedo());
        undoButton.setDisable(!topController.canUndo());
        requestLayout();
    }


}


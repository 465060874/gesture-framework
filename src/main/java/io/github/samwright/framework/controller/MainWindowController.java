package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.example.ExContainerController;
import io.github.samwright.framework.controller.example.ExElementController;
import io.github.samwright.framework.controller.example.StartElementController;
import io.github.samwright.framework.controller.example.ViewerController;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.controller.helper.IntegerViewer;
import io.github.samwright.framework.controller.helper.PreviewPane;
import io.github.samwright.framework.javacv.*;
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

    @FXML
    private Button trainButton;

    @FXML
    private Button processButton;

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
        processButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                topController.getModel().process(null);
//                topController.handleUpdatedModel();
            }
        });

        toolboxController = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        toolboxController.setStyle("-fx-vgap: 5;-fx-hgap: 5;-fx-padding: 5");
        hBox.getChildren().add(toolboxController);

        topController.handleUpdatedModel();
        topController.addNewWorkflow();
        ModelLoader.registerPrototypeModel(topController.getModel());
        setOnKeyPressed(topController.getKeyPressHandler());

        ModelLoader.registerPrototypeModel(new ExElementController().getModel());
        ModelLoader.registerPrototypeModel(new ExContainerController().getModel());
        ModelLoader.registerPrototypeModel(new WorkflowControllerImpl().getModel());
        ModelLoader.registerPrototypeModel(new StartElementController().getModel());
        ModelLoader.registerPrototypeModel(new ViewerController().getModel());
        ModelLoader.registerPrototypeModel(new ImageLoaderController().getModel());
        ModelLoader.registerPrototypeModel(new SkinDetectorController().getModel());
        ModelLoader.registerPrototypeModel(new StaticColourRangeController().getModel());

        PreviewPane.registerDataViewer(new IntegerViewer());
        PreviewPane.registerDataViewer(new ImageViewer());
        PreviewPane.registerDataViewer(new ColourRangeViewer());


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


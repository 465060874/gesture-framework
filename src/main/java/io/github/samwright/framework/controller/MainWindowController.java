package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.actors.KeyboardActorController;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.controller.helper.PreviewPane;
import io.github.samwright.framework.javacv.*;
import io.github.samwright.framework.javacv.viewers.*;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.helper.ModelLoader;
import io.github.samwright.framework.model.helper.XMLHelper;
import io.github.samwright.framework.model.mock.TopProcessor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.File;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:55
 */
public class MainWindowController extends VBox {

    @FXML
    private ScrollPane mainScrollPanel, toolboxScrollPane;

    @FXML
    private HBox hBox;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Button undoButton, redoButton, trainButton,
                   processButton, saveButton, saveAsButton, openButton;


    @Getter private static TopContainerController topController;
    private static ToolboxController toolboxController;
    private String filename;

    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        setTopController(new TopContainerController(this));

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
                topController.getModel().process();
                handleUpdatedModel();
            }
        });
        trainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
//                trainButton.setDisable(true);
                topController.getModel().train();
                handleUpdatedModel();
            }
        });
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                    save();
            }
        });

        saveAsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                saveAs();
            }
        });
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                open();
            }
        });

        toolboxController = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        toolboxController.setStyle("-fx-vgap: 5;-fx-hgap: 5;-fx-padding: 5");
        toolboxScrollPane.setContent(toolboxController);
        SplitPane.setResizableWithParent(toolboxScrollPane, false);

        topController.handleUpdatedModel();
        topController.addNewWorkflow();
        ModelLoader.registerPrototypeModel(topController.getModel());

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                topController.handleKeyEvent(keyEvent);
            }
        });

//        ModelLoader.registerPrototypeModel(new ExElementController().getModel());
//        ModelLoader.registerPrototypeModel(new ExContainerController().getModel());
        ModelLoader.registerPrototypeModel(new WorkflowControllerImpl().getModel());
//        ModelLoader.registerPrototypeModel(new StartElementController().getModel());
//        ModelLoader.registerPrototypeModel(new ViewerController().getModel());
        ModelLoader.registerPrototypeModel(new ImageLoaderController().getModel());
        ModelLoader.registerPrototypeModel(new SkinDetectorController().getModel());
        ModelLoader.registerPrototypeModel(new StaticColourRangeController().getModel());
        ModelLoader.registerPrototypeModel(new ContourFinderController().getModel());
//        ModelLoader.registerPrototypeModel(new ConvexHullFinderController().getModel());
        ModelLoader.registerPrototypeModel(new HandDetectorController().getModel());
        ModelLoader.registerPrototypeModel(new FingertipFinderController().getModel());
        ModelLoader.registerPrototypeModel(new PalmDetectorController().getModel());
        ModelLoader.registerPrototypeModel(new FingertipReducerController().getModel());
        ModelLoader.registerPrototypeModel(new SimplifyContourController().getModel());
//        ModelLoader.registerPrototypeModel(new DominantPointFinderController().getModel());
        ModelLoader.registerPrototypeModel(new NNClassifierController().getModel());
        ModelLoader.registerPrototypeModel(new OptimiserController().getModel());
        ModelLoader.registerPrototypeModel(new KeyboardActorController().getModel());


//        PreviewPane.registerDataViewer(new StringViewer());
        PreviewPane.registerDataViewer(new ImageViewer());
        PreviewPane.registerDataViewer(new ColourRangeViewer());
        PreviewPane.registerDataViewer(new ContourViewer());
        PreviewPane.registerDataViewer(new PalmViewer());
        PreviewPane.registerDataViewer(new FingertipViewer());
        PreviewPane.registerDataViewer(new HandViewer());
        PreviewPane.registerDataViewer(new ClassificationViewer());


        for (Processor p : ModelLoader.getAllProtoypeModels())
            if (p instanceof Element && !(p instanceof TopWorkflowContainer))
                toolboxController.getChildren().add((Node) p.getController());
    }

    private void setTopController(TopContainerController topController) {
        MainWindowController.topController = topController;
        mainScrollPanel.setContent(topController);

        TopProcessor pointer = topController.getModel();
        while (pointer != null) {
            pointer.setTransientModel(true);
            pointer = (TopProcessor) pointer.getPrevious();
        }
        topController.getModel().setTransientModel(false);
        handleUpdatedModel();
    }

    public void handleUpdatedModel() {
        redoButton.setDisable(!topController.canRedo());
        undoButton.setDisable(!topController.canUndo());
        processButton.setDisable(!topController.canProcess());
        trainButton.setDisable(!topController.canTrain());

        requestLayout();
    }

    public void save() {
        if (filename == null) {
            saveAs();
        } else {
            XMLHelper.writeProcessorToFile(topController.getModel(), filename);
        }
    }

    public void saveAs() {
        File file = new FileChooser().showSaveDialog(MainApp.getStage());
        if (file == null)
            return;

        if (file.exists())
            file.delete();
        filename = file.getAbsolutePath();
        save();
    }

    public void open() {
        File file = new FileChooser().showOpenDialog(MainApp.getStage());
        if (file == null)
            return;
        filename = file.getAbsolutePath();
        TopWorkflowContainer loaded = (TopWorkflowContainer)
                XMLHelper.loadProcessorFromFile(file.getAbsolutePath(), false);


        setTopController((TopContainerController) loaded.getController());
        topController.startTransientUpdateMode();
        try {
            topController.getModel().discardPrevious();
        } finally {
            topController.endTransientUpdateMode();
        }
    }
}


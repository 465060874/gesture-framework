package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.common.Replaceable;
import io.github.samwright.framework.model.helper.TypeData;
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

    @Getter private WorkflowContainerController topModelController;

    public MainWindowController() {
        Controllers.bindViewToController("/fxml/MainWindow.fxml", this);

        TypeData<Integer, Integer> type = new TypeData<>(Integer.class, Integer.class);
        Replaceable model = new TopWorkflowContainer<>(type);
        topModelController = new WorkflowContainerController(model);
        mainScrollPanel.setContent(topModelController);

    }

}

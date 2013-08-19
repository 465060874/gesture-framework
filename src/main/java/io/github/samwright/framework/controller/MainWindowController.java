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

        topModel = (TopWorkflowContainer) MainApp.beanFactory.getBean("topWorkflowContainer");
        mainScrollPanel.setContent(topModel.getController());
        topController = topModel.getController();

        toolboxController = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        getChildren().add(toolboxController);

//        Workflow emptyWorkflow = (Workflow) MainApp.beanFactory.getBean("workflow");
//
//
//        TypeData<String,String> typeData = new TypeData<>(String.class, String.class);
//
//        Element element1 = (Element) MainApp.beanFactory.getBean("mockElement1");
//        Element element2 = (Element) MainApp.beanFactory.getBean("mockElement2");
//        Element element3 = (Element) MainApp.beanFactory.getBean("mockElement1");
//        Element element4 = (Element) MainApp.beanFactory.getBean("mockElement2");
//
//
//        Workflow workflow = emptyWorkflow
//                .withTypeData(topModel.getTypeData())
//                .withParent(topModel)
//                .withChildren(Arrays.asList(element1, element2, element3, element4));
//
//        emptyWorkflow.replaceWith(workflow);
//
//
//        // Setup toolbox
//        element1 = (Element) MainApp.beanFactory.getBean("mockElement1");
//        element2 = (Element) MainApp.beanFactory.getBean("mockElement2");
//
//        toolbox.getChildren().addAll(Arrays.asList(
//                element1.getController(), element2.getController()
//        ));

//        element1.getController().setOnDragDetected();
    }

}


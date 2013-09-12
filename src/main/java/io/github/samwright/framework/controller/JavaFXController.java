package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.Processor;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class JavaFXController extends VBox implements ModelController {

    @Getter private Processor model, proposedModel;
    @Getter private final String fxmlResource;
    @Setter @Getter private boolean isBeingDragged = false;
    @Getter private boolean selected = false;

    {
        updateColours();
    }

    public JavaFXController(@NonNull String fxmlResource) {
        this.fxmlResource = fxmlResource;
        Controllers.bindViewToController(fxmlResource, this);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MainWindowController.getTopController()
                        .handleClick(JavaFXController.this, mouseEvent);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public JavaFXController(JavaFXController toClone) {
        this(toClone.getFxmlResource());
        this.model = null;
        selected = toClone.isSelected();
    }

    @Override
    public void proposeModel(Processor proposedModel) {
        if (proposedModel != null) {
            this.proposedModel = proposedModel;
            if (proposedModel.getController() != this)
                proposedModel.setController(this);
        }
    }

    @Override
    public void handleUpdatedModel() {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        if (toolbox.getChildren().contains(this)) {
            model.setAsCurrentVersion();
        } else {
            if (proposedModel != null)
                model = proposedModel;
            proposedModel = null;
            updateColours();
        }

    }

    private void updateColours() {
        if (model != null && !model.isValid()) {
            if (isSelected())
                setStyle("-fx-background-color: #c7537f");
            else
                setStyle("-fx-background-color: #cd9686");
        } else {
            if (isSelected())
                setStyle("-fx-background-color: lightblue");
            else
                setStyle("-fx-background-color: #f0f0f0");
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateColours();
    }
}

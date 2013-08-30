package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends EventuallyImmutable> extends VBox {

    @Getter private T model, proposedModel, previousModel;
    @Getter private final String fxmlResource;
    @Setter @Getter private boolean isBeingDragged = false;
    @Getter private BooleanProperty clickedProperty;

    {
        setStyle("-fx-background-color: #f0f0f0");
        clickedProperty = new SimpleBooleanProperty();
        clickedProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldVal,
                                Boolean newVal) {
                if (newVal)
                    setStyle("-fx-background-color: lightblue");
                else
                    setStyle("-fx-background-color: #f0f0f0");
            }
        });
    }

    public ModelController(@NonNull String fxmlResource) {
        this.fxmlResource = fxmlResource;
        Controllers.bindViewToController(fxmlResource, this);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                MainWindowController.getTopController()
                        .handleClick(ModelController.this, mouseEvent);
            }
        });
        clickedProperty.set(false);
    }

    @SuppressWarnings("unchecked")
    public ModelController(ModelController<T> toClone) {
        this(toClone.getFxmlResource());
        this.model = null;
        clickedProperty.set(toClone.clickedProperty.get());
    }

    public void proposeModel(T proposedModel) {
        if (proposedModel != model && proposedModel != null)
            this.proposedModel = proposedModel;
    }

    abstract public ModelController<T> createClone();

    public void handleUpdatedModel() {
        previousModel = model;
        if (proposedModel != null)
            model = proposedModel;
        proposedModel = null;
        if (model.getController() != this)
            model.setController(this);
    }

    public void revertModel() {
        proposeModel(getPreviousModel());
        handleUpdatedModel();
    }
}

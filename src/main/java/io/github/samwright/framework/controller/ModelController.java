package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends EventuallyImmutable> extends VBox {

    @Getter private T model;

    @Getter private final String fxmlResource;

    @Setter @Getter private boolean isBeingDragged = false;

    public ModelController(@NonNull String fxmlResource) {
        this.fxmlResource = fxmlResource;
        Controllers.bindViewToController(fxmlResource, this);
    }

    @SuppressWarnings("unchecked")
    public ModelController(ModelController<T> toClone) {
        this(toClone.getFxmlResource());
        this.model = null;
    }

    public void setModel(T model) {
        if (this.model != model) {
            this.model = model;
            if (this.model.getController() != this)
                model.setController(this);
        }
    }

    abstract public ModelController<T> createClone();

    abstract public void handleUpdatedModel();
}

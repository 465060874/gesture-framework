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

    @Setter @Getter private T model;

    public ModelController(@NonNull String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);
    }

    @SuppressWarnings("unchecked")
    public ModelController(ModelController<T> toClone) {
        this.model = (T) toClone.getModel().createMutableClone();
        this.model.fix();
    }

    abstract public ModelController<T> createClone();

    abstract public void handleUpdatedModel();
}

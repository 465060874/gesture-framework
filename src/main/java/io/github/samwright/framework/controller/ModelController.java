package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.Replaceable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends Replaceable> extends VBox {

    @Setter @Getter private T model;

    public ModelController(@NonNull String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);
    }

    abstract public void handleUpdatedModel();
}

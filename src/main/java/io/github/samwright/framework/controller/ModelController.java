package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.Replaceable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends Replaceable> extends VBox {

    @Getter private T model;

    public ModelController(@NonNull String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);
    }

    public void setModel(@NonNull T model) {
        System.out.println("model-controller:0");
        if (this.model != model) {
            System.out.println("model-controller:1");
            this.model = model;
            System.out.println("model-controller:2");
//            handleUpdatedModel();
//            System.out.println("model-controller:5");
        }
    }

    abstract public void handleUpdatedModel();
}

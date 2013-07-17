package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.Replaceable;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 18:59
 */
abstract public class AbstractModelController extends VBox implements ModelController {

    @Getter private Replaceable model;

    public AbstractModelController(String fxmlResource, Replaceable model) {
        Controllers.bindViewToController(fxmlResource, this);
        model.setController(this);
        notify(model);
    }

    @Override
    public void notify(Replaceable model) {
        this.model = model.versionInfo().getLatest();
    }
}

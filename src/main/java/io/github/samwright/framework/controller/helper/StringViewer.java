package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.model.helper.Mediator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 14:23
 */
public abstract class StringViewer extends DataViewer {

    private Label label;

    public StringViewer() {
        label = new Label("No data");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(label);
    }

    @Override
    public void view(Mediator mediator) {
        label.setText(getString(mediator));
    }

    public abstract String getString(Mediator mediator);
}

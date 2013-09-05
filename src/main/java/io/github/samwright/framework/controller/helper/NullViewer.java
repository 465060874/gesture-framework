package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.model.helper.Mediator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 13:57
 */
public class NullViewer extends DataViewer {

    public NullViewer() {
        Label label = new Label("No viewer set!");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(label);
    }

    @Override
    public DataViewer createClone() {
        return new NullViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return null;
    }

    @Override
    public void view(Mediator mediator) {
    }
}

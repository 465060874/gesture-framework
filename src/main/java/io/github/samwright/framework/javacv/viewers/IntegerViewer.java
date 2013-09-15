package io.github.samwright.framework.javacv.viewers;

import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 14:23
 */
public class IntegerViewer extends DataViewer {


    private Label label;

    public IntegerViewer() {
        label = new Label("No data");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(label);
    }

    @Override
    public DataViewer createClone() {
        return new IntegerViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Integer.class;
    }

    @Override
    public void view(Mediator mediator) {
        Integer data = (Integer) mediator.getData();
        label.setText(String.valueOf(data));
    }

    @Override
    public String toString() {
        return "Integer";
    }
}
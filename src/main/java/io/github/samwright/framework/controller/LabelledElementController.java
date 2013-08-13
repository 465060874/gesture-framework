package io.github.samwright.framework.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:48
 */
public class LabelledElementController<I, O> extends ElementController<I, O> {

    @FXML
    private VBox element;

    @FXML
    private Label label;

    public LabelledElementController(String fxmlString, String labelString) {
        super(fxmlString);
        label.setText(labelString);
    }

    @Override
    public void handleUpdatedModel() {
        // Dummy implementation
    }
}

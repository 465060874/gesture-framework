package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:48
 */
public class LabelledElementController extends ElementController {

    @FXML
    private VBox element;

    @FXML
    private Label label;

    public LabelledElementController(String labelString) {
        super("/fxml/LabelledElement.fxml");
        label.setText(labelString);
    }

    public LabelledElementController(LabelledElementController toClone) {
        super(toClone);
    }

    @Override
    public ModelController<Element> createClone() {
        return new LabelledElementController(this);
    }

    @Override
    public void handleUpdatedModel() {
        // Dummy implementation
    }
}

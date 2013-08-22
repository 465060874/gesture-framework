package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:48
 */
public class ExampleController extends ElementController {

    @FXML
    private VBox element;

    @FXML
    private Label label;

    public ExampleController(String labelString) {
        super("/fxml/LabelledElement.fxml");
        label.setText(labelString);
        setModel(new ExampleModel());
        setElementLink(new ElementLink());
    }

    public ExampleController(ExampleController toClone) {
        super(toClone);
        label.setText(toClone.label.getText());
    }

    @Override
    public ModelController<Element> createClone() {
        return new ExampleController(this);
    }

    @Override
    public void handleUpdatedModel() {
        // Dummy implementation
    }

    @Override
    public String toString() {
        return label.getText() + hashCode();
    }
}

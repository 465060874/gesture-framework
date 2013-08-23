package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button clickButton;

    @FXML
    private Label clicksLabel;


    public ExampleController(String labelString) {
        super("/fxml/LabelledElement.fxml");
        label.setText(labelString);
        setModel(new ExampleModel());
        setElementLink(new ElementLink());
        setupView();
    }

    public ExampleController(ExampleController toClone) {
        super(toClone);
        label.setText(toClone.label.getText());
        setupView();
    }

    private void setupView() {

        clickButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int newClicks = getModel().getClicks() + 1;
                ExampleModel newModel = getModel().withClicks(newClicks);
                getModel().replaceWith(newModel);
            }
        });
    }

    @Override
    public ModelController<Element> createClone() {
        return new ExampleController(this);
    }

    @Override
    public void handleUpdatedModel() {
        String labelText = getModel().getClicks() + " clicks";
        clicksLabel.setText(labelText);
    }

    @Override
    public ExampleModel getModel() {
        return (ExampleModel) super.getModel();
    }

    @Override
    public String toString() {
        return label.getText() + hashCode();
    }
}

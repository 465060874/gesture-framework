package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:48
 */
public class ExElementController extends ElementController {

    @FXML
    private VBox element;

    @FXML
    private Label label;

    @FXML
    private Button clickButton;

    @FXML
    private Label clicksLabel;


    public ExElementController(String labelString) {
        super("/fxml/LabelledElement.fxml");
        label.setText(labelString);
        setModel(new ExElement(labelString));
        setElementLink(new ElementLink());
        setupView();
    }

    public ExElementController(ExElementController toClone) {
        super(toClone);
        label.setText(toClone.label.getText());
        setupView();
    }

    private void setupView() {

        clickButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int newClicks = getModel().getClicks() + 1;
                ExElement newModel = getModel().withClicks(newClicks);
                getModel().replaceWith(newModel);
            }
        });
    }

    @Override
    public ExElementController createClone() {
        return new ExElementController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        String labelText = getModel().getClicks() + " clicks";
        clicksLabel.setText(labelText);
    }

    @Override
    public ExElement getModel() {
        return (ExElement) super.getModel();
    }

    @Override
    public String toString() {
        return label.getText() + hashCode();
    }

}

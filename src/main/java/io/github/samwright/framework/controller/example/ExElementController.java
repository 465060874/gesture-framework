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

    {
        clickButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int newClicks = getModel().getClicks() + 1;
                Adder newModel = getModel().withClicks(newClicks);
                getModel().replaceWith(newModel);
            }
        });
    }


    public ExElementController() {
        super("/fxml/LabelledElement.fxml");
        proposeModel(new Adder());
        setElementLink(new ElementLink());
        handleUpdatedModel();
    }

    public ExElementController(ExElementController toClone) {
        super(toClone);
    }

    @Override
    public ExElementController createClone() {
        return new ExElementController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        clicksLabel.setText("add " + getModel().getClicks());
    }

    @Override
    public Adder getModel() {
        return (Adder) super.getModel();
    }

    @Override
    public String toString() {
        return label.getText() + hashCode();
    }

}

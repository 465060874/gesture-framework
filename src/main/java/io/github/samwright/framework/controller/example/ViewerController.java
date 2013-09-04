package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 18:02
 */
public class ViewerController extends ElementController {

    @FXML
    private Label label;

    public ViewerController() {
        super("/fxml/SimpleElement.fxml");
        setElementLink(new ElementLink());
        proposeModel(new Viewer());
    }

    public ViewerController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new ViewerController(this);
    }

    @Override
    public Viewer getModel() {
        return (Viewer) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        label.setText("Viewing: " + getModel().getPreviousOutput());
    }
}

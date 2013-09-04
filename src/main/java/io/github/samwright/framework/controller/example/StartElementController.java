package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 17:43
 */
public class StartElementController extends ElementController {

    @FXML
    private Label label;

    {
        label.setText("Start Element");
    }

    public StartElementController() {
        super("/fxml/SimpleElement.fxml");
        setElementLink(new ElementLink());
        proposeModel(new StartElement());
    }

    public StartElementController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new StartElementController(this);
    }
}

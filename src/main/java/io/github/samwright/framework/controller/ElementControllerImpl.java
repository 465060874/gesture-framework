package io.github.samwright.framework.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:48
 */
public class ElementControllerImpl<I, O> extends ElementController<I, O> {

    @FXML
    private VBox element;

    public ElementControllerImpl(String fxmlString) {
//        super("/fxml/Element.fxml", model);
        super(fxmlString);

        System.out.println("element = " + element);
    }

    @Override
    public void handleUpdatedModel() {
        // Dummy implementation
    }
}

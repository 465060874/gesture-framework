package io.github.samwright.framework.controller.helper;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 13:56
 */
public class ElementLink extends Pane {

    @FXML
    private Line line;

    @FXML
    private Polygon triangle;

    public ElementLink(String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);

        line.setStartX(0);
        line.endXProperty().bind(widthProperty());
        line.startYProperty().bind(heightProperty().divide(2));
        line.endYProperty().bind(heightProperty().divide(2));

        triangle.translateXProperty().bind(widthProperty().divide(2));
        triangle.translateYProperty().bind(heightProperty().divide(2));
    }

}

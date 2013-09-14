package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 20:41
 */
public class SimplifyContourController extends ElementController {
    @FXML
    private VBox configPane;

    @FXML
    private Slider accuracySlider;

    private boolean changing = false;

    {
        accuracySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldVal,
                    Number newVal) {

                if (!changing)
                    getModel().replaceWith(getModel().withAccuracy(newVal.doubleValue()));
            }
        });

        addConfigNode(configPane);
    }

    public SimplifyContourController() {
        super("/fxml/SimplifyContour.fxml");
        proposeModel(new SimplifyContour());
        setElementLink(new ElementLink());
    }

    public SimplifyContourController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new SimplifyContourController(this);
    }

    @Override
    public SimplifyContour getModel() {
        return (SimplifyContour) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        changing = true;
        accuracySlider.setValue(getModel().getAccuracy());
        changing = false;
    }
}

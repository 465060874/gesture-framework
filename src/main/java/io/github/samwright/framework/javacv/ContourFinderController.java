package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 18:41
 */
public class ContourFinderController extends ElementController {

    @FXML
    private HBox configPanel;

    @FXML
    private Slider lowerLimit, upperLimit;
    private boolean changing = true;

    {
        lowerLimit.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number oldVal,
                                Number newVal) {
                if (!changing)
                    getModel().replaceWith(getModel().withLowerLimit(newVal.intValue()));
            }
        });

        upperLimit.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number oldVal,
                                Number newVal) {
                if (!changing)
                    getModel().replaceWith(getModel().withUpperLimit(newVal.intValue()));
            }
        });

        addConfigNode(configPanel);
    }

    public ContourFinderController() {
        super("/fxml/ContourFinder.fxml");
        proposeModel(new ContourFinder());
        setElementLink(new ElementLink());
    }

    public ContourFinderController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ContourFinderController createClone() {
        return new ContourFinderController(this);
    }

    @Override
    public ContourFinder getModel() {
        return (ContourFinder) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        changing = true;
        lowerLimit.setValue(getModel().getLowerLimit());
        upperLimit.setValue(getModel().getUpperLimit());
        changing = false;
    }
}

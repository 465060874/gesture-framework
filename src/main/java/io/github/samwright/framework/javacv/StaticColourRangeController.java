package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Wright Date: 08/09/2013 Time: 23:22
 */
public class StaticColourRangeController extends ElementController {

    @FXML
    private Label label;

    @FXML
    private VBox configPane;

    @FXML
    private Slider upperH, upperS, upperV, lowerH, lowerS, lowerV;

    {
        ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldVal, Boolean newVal) {
                if (!newVal) {
                    List<Double> upperRange = new ArrayList<>();
                    upperRange.add(upperH.getValue());
                    upperRange.add(upperS.getValue());
                    upperRange.add(upperV.getValue());
                    List<Double> lowerRange = new ArrayList<>();
                    lowerRange.add(lowerH.getValue());
                    lowerRange.add(lowerS.getValue());
                    lowerRange.add(lowerV.getValue());

                    StaticColourRange replacement = getModel().withRanges(upperRange, lowerRange);
                    getModel().replaceWith(replacement);
                }

            }
        };

        Slider[] sliders = {upperH, upperS, upperV, lowerH, lowerS, lowerV};
        for (Slider slider : sliders)
            slider.valueChangingProperty().addListener(changeListener);

        addConfigNode(configPane);
    }

    public StaticColourRangeController() {
        super("/fxml/ColourRange.fxml");
        proposeModel(new StaticColourRange());
        setElementLink(new ElementLink());
    }

    public StaticColourRangeController(StaticColourRangeController toClone) {
        super(toClone);
        label.setText(toClone.label.getText());
    }

    private void updateSliders() {
        if (getModel() != null) {
            upperH.setValue(getModel().getUpperRange().get(0));
            upperS.setValue(getModel().getUpperRange().get(1));
            upperV.setValue(getModel().getUpperRange().get(2));
            lowerH.setValue(getModel().getLowerRange().get(0));
            lowerS.setValue(getModel().getLowerRange().get(1));
            lowerV.setValue(getModel().getLowerRange().get(2));
        }
    }

    @Override
    public ElementController createClone() {
        return new StaticColourRangeController(this);
    }

    @Override
    public StaticColourRange getModel() {
        return (StaticColourRange) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        updateSliders();
    }
}

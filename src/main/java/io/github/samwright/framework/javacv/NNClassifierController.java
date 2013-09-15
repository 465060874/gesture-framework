package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.javacv.helper.HistoryHighlighter;
import io.github.samwright.framework.model.helper.History;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 18:29
 */
public class NNClassifierController extends ElementController {

    @FXML
    private VBox successRatesBox;

    {
        addConfigNode(successRatesBox);
    }

    public NNClassifierController() {
        super("/fxml/NNClassifier.fxml");
        proposeModel(new NNClassifier());
        setElementLink(new ElementLink());
    }

    public NNClassifierController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new NNClassifierController(this);
    }

    @Override
    public NNClassifier getModel() {
        return (NNClassifier) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        showTrainingStats();
    }

    @Override
    public void handleTrained() {
        super.handleTrained();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showTrainingStats();
            }
        });
    }

    private void showTrainingStats() {
        successRatesBox.getChildren().clear();

        if (getModel().getSuccessRates() == null)
            return;

        successRatesBox.getChildren().add(new Label("Success rates"));

        for (Map.Entry<History, Double> e : getModel().getSuccessRates().entrySet()) {
            final History history = e.getKey();
            Double successRate = e.getValue();

            String label = String.format("%.1f%%", successRate * 100);
            successRatesBox.getChildren().add(new HistoryHighlighter(history, this, label));
        }
    }
}

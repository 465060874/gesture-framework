package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.JavaFXController;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.History;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 18:29
 */
public class NNClassifierController extends ElementController {

    @FXML
    private VBox successRatesBox;

    {
        setConfigNode(successRatesBox);
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

        successRatesBox.getChildren().clear();

        if (getModel().getSuccessRates() == null)
            return;

        successRatesBox.getChildren().add(new Label("Success rates"));

        for (Map.Entry<History,Double> e : getModel().getSuccessRates().entrySet()) {
            final History history = e.getKey();
            Double successRate = e.getValue();

            Button button = new Button(String.format("%.1f%%", successRate * 100));
            successRatesBox.getChildren().add(button);

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Set<JavaFXController> historicProcessors = new HashSet<>();
                    for (Processor creator : History.getAllCreators(history))
                        historicProcessors.add((JavaFXController) creator.getController());

                    historicProcessors.add(NNClassifierController.this);
                    MainWindowController.getTopController().setSelection(historicProcessors);
                }
            });
        }
    }
}

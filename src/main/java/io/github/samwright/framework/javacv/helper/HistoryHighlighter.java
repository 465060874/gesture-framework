package io.github.samwright.framework.javacv.helper;

import io.github.samwright.framework.controller.JavaFXController;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.History;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright Date: 14/09/2013 Time: 13:46
 */
public class HistoryHighlighter extends Button {
    public HistoryHighlighter(final History history, final JavaFXController controller, String label) {
        super(label);

        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Set<JavaFXController> historicProcessors = new HashSet<>();
                for (Processor creator : History.getAllCreators(history))
                    historicProcessors.add((JavaFXController) creator.getController());

                historicProcessors.add(controller);
                MainWindowController.getTopController().setSelection(historicProcessors);
            }
        });
    }
}

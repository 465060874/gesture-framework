package io.github.samwright.framework.actors;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Sam Wright Date: 13/09/2013 Time: 12:24
 */
public class KeyboardActorController extends ElementController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private CheckBox enabledCheckBox;

    {
        addConfigNode(scrollPane);

        enabledCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldVal,
                                Boolean newVal) {

                getModel().setActive(newVal);
            }
        });
    }

    public KeyboardActorController() {
        super("/fxml/KeyboardActor.fxml");
        proposeModel(new KeyboardActor());
        setElementLink(new ElementLink());
    }

    public KeyboardActorController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new KeyboardActorController(this);
    }

    @Override
    public KeyboardActor getModel() {
        return (KeyboardActor) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        gridPane.getChildren().clear();
        enabledCheckBox.setSelected(getModel().isActive());

        int i = 0;
        for (Map.Entry<String,String> entry : getModel().getKeyCodes().entrySet()) {
            String tag = entry.getKey();
            String key = entry.getValue();

            Label tagLabel = new Label(tag);
            tagLabel.setMinWidth(90);
            gridPane.add(tagLabel, 0, i);
            gridPane.add(new KeyInputField(tag, key), 1, i);

            ++i;
        }
    }

    private class KeyInputField extends TextField {

        private final String key;

        public KeyInputField(final String tag, final String key) {
            super(key);
            this.key = key;

            setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
                @Override
                public void handle(javafx.scene.input.KeyEvent keyEvent) {
                    keyEvent.getCode().impl_getCode();
                    String newKey = keyEvent.getCode().getName();
                    if (!newKey.equals(key)) {
                        Map<String,String> newKeyMap = new HashMap<>(getModel().getKeyCodes());
                        if (!newKeyMap.containsKey(tag))
                            throw new RuntimeException("Tag wasn't in original key map!");

                        newKeyMap.put(tag, newKey);
                        getModel().replaceWith(getModel().withKeyCodes(newKeyMap));

                        keyEvent.consume();
                    }
                }
            });
        }

        @Override
        public void replaceText(IndexRange indexRange, String s) {
            if (key == null)
                super.replaceText(indexRange, s);
        }

        @Override
        public void replaceText(int i, int i2, String s) {
            if (key == null)
                super.replaceText(i, i2, s);
        }

        @Override
        public void replaceSelection(String s) {
            if (key == null)
                super.replaceSelection(s);
        }
    }
}

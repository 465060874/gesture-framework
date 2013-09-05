package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.model.helper.Mediator;
import javafx.scene.layout.Pane;
import lombok.Getter;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 13:25
 */
abstract public class DataViewer extends Pane {

    public static class ComboItem {
        @Getter private final DataViewer dataViewer;

        public ComboItem(DataViewer dataViewer) {
            this.dataViewer = dataViewer;
        }

        @Override
        public String toString() {
            return dataViewer.toString();
        }
    }

    @Getter private final ComboItem comboItem = new ComboItem(this);

    abstract public DataViewer createClone();

    abstract public Class<?> getViewableClass();

    public abstract void view(Mediator mediator);
}

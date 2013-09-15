package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.model.datatypes.ClassHelper;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 12:16
 */
public class PreviewPane extends VBox {

    private final static Map<Class, DataViewer> dataViewers = new HashMap<>();

    public static void registerDataViewer(DataViewer dataViewer) {
        dataViewers.put(dataViewer.getViewableClass(), dataViewer);
    }

    @FXML
    private ComboBox<DataViewer.ComboItem> viewerSelector;

    @FXML
    private VBox dataViewerBox;

    private DataViewer selectedViewer;
    private Class<?> inputType;
    private Mediator lastMediator;

    public PreviewPane() {
        Controllers.bindViewToController("/fxml/PreviewPane.fxml", this);

        viewerSelector.valueProperty().addListener(new ChangeListener<DataViewer.ComboItem>() {
            @Override
            public void changed(ObservableValue<? extends DataViewer.ComboItem> observableValue,
                                DataViewer.ComboItem oldSelection,
                                DataViewer.ComboItem newSelection) {
                setDataViewer(newSelection.getDataViewer());
            }
        });

        visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean oldVal,
                                Boolean newVal) {
                if (newVal)
                    updateDataViewer();
            }
        });
    }

    public void setInputType(Class inputType) {
        if (inputType.equals(this.inputType))
            return;

        this.inputType = inputType;
        viewerSelector.getItems().clear();

        for (Class clazz : ClassHelper.getAncestry(inputType)) {
            DataViewer dataViewer = dataViewers.get(clazz);
            if (dataViewer != null)
                viewerSelector.getItems().add(dataViewer.createClone().getComboItem());
        }

        if (selectedViewer != null
                && selectedViewer.getViewableClass().isAssignableFrom(inputType)) {

            DataViewer newDataViewer = null;
            for (DataViewer.ComboItem dataViewerComboItem : viewerSelector.getItems()) {
                DataViewer dataViewer = dataViewerComboItem.getDataViewer();
                if (dataViewer.getViewableClass().equals(selectedViewer.getViewableClass())) {
                    newDataViewer = dataViewer;
                    break;
                }
            }
            if (newDataViewer == null)
                viewerSelector.getSelectionModel().selectFirst();
            else
                viewerSelector.getSelectionModel().select(newDataViewer.getComboItem());
        } else {
            viewerSelector.getSelectionModel().selectFirst();
        }

        updateDataViewer();
    }

    private void setDataViewer(DataViewer dataViewer) {
        if (dataViewer == null)
            dataViewer = new NullViewer();

        this.selectedViewer = dataViewer;
        dataViewerBox.getChildren().clear();
        dataViewerBox.getChildren().add(dataViewer);
        updateDataViewer();
    }

    public void updateDataViewer() {
        if (lastMediator == null)
            return;

        setInputType(lastMediator.getData().getClass());

        if (selectedViewer != null)
            selectedViewer.view(lastMediator);
    }

    public void handleProcessedData(Mediator processedData) {
        this.lastMediator = processedData;
        if (isVisible())
            updateDataViewer();
    }
}

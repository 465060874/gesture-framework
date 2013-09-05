package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.datatypes.Helper;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 12:16
 */
public class PreviewPane extends VBox implements ElementObserver {

    private final static Map<Class, DataViewer> dataViewers = new HashMap<>();

    public static void registerDataViewer(DataViewer dataViewer) {
        dataViewers.put(dataViewer.getViewableClass(), dataViewer);
    }

    @FXML
    private ComboBox<DataViewer.ComboItem> viewerSelector;

    @FXML
    private Button firstButton, prevButton, nextButton, lastButton;

    @FXML
    private Label countLabel, defaultViewer;

    @FXML
    private HBox footer;

    @FXML
    private VBox dataViewerBox;

    private DataViewer selectedViewer;
    private Class<?> inputType;
    private Mediator lastMediator;
    private List<Mediator> lastMediatorList;
    private boolean lastWasSingleMediator = true;
    private int mediatorListIndex = -1;

    public PreviewPane() {
        Controllers.bindViewToController("/fxml/PreviewPane.fxml", this);
        setFooterVisible(false);

        viewerSelector.valueProperty().addListener(new ChangeListener<DataViewer.ComboItem>() {
            @Override
            public void changed(ObservableValue<? extends DataViewer.ComboItem> observableValue,
                                DataViewer.ComboItem oldSelection,
                                DataViewer.ComboItem newSelection) {
                setDataViewer(newSelection.getDataViewer());
            }
        });

    }

    public void setInputType(Class inputType) {
        if (inputType.equals(this.inputType))
            return;

        this.inputType = inputType;
        viewerSelector.getItems().clear();

        for (Class clazz : Helper.getAncestry(inputType)) {
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
        Mediator mediator;

        if (lastWasSingleMediator)
            mediator = lastMediator;
        else
            mediator = lastMediatorList.get(mediatorListIndex);

        if (mediator == null)
            return;

        setInputType(mediator.getClass());

        if (selectedViewer != null)
            selectedViewer.view(mediator);
    }

    @Override
    public void notify(Mediator mediator) {
        this.lastMediator = mediator;
        lastWasSingleMediator = true;
        setFooterVisible(false);
        updateDataViewer();
    }

    @Override
    public void notify(List<Mediator> mediators) {
        this.lastMediatorList = mediators;
        mediatorListIndex = 0;
        lastWasSingleMediator = false;
        setFooterVisible(true);
        updateDataViewer();
    }

    private void setFooterVisible(boolean visible) {
        if (visible) {
            if (!getChildren().contains(footer))
                getChildren().add(footer);
            countLabel.setText(String.format("%d / %d", mediatorListIndex, lastMediatorList.size()));
        } else {
            if (getChildren().contains(footer))
                getChildren().remove(footer);
        }
    }
}

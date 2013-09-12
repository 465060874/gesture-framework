package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.javacv.helper.LoadedImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 11:44
 */
public class ImageLoaderController extends ElementController {

    @FXML
    private TextField tagField;

    @FXML
    private Button snapshotButton;

    @FXML
    private ListView<LoadedImage> imagesView;

    private boolean editingImages = false;

    {
        imagesView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LoadedImage>() {
            @Override
            public void changed(ObservableValue<? extends LoadedImage> observableValue,
                                LoadedImage oldVal, LoadedImage newVal) {
                if (!editingImages) {
                    getModel().setActiveImage(newVal);
                    MainWindowController.getTopController().getModel().process();
                }
            }
        });

        snapshotButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getModel().takeSnapshot(tagField.getText());
            }
        });

        setConfigNode(imagesView);
    }

    public ImageLoaderController() {
        super("/fxml/ImageLoader.fxml");
        proposeModel(new ImageLoader());
        setElementLink(new ElementLink());
    }

    public ImageLoaderController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new ImageLoaderController(this);
    }

    @Override
    public ImageLoader getModel() {
        return (ImageLoader) super.getModel();
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        editingImages = true;
        imagesView.getItems().clear();
        imagesView.getItems().addAll(getModel().getImages());
        editingImages = false;

        LoadedImage activeImage = getModel().getActiveImage();
        if (activeImage != null)
            imagesView.getSelectionModel().select(activeImage);
        else
            imagesView.getSelectionModel().clearSelection();
    }
}

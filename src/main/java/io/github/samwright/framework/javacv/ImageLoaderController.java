package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.helper.ElementLink;
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
    private ListView<TaggedImage> imagesView;

    private boolean editingImages = false;

    {
        imagesView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TaggedImage>() {
            @Override
            public void changed(ObservableValue<? extends TaggedImage> observableValue,
                                TaggedImage oldVal, TaggedImage newVal) {
                if (!editingImages) {
                    getModel().setActiveImage(newVal);
                    MainWindowController.getTopController().getModel().process(null);
                }
            }
        });

        snapshotButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getModel().takeSnapshot(tagField.getText());
            }
        });
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

        TaggedImage activeImage = getModel().getActiveImage();
        if (activeImage != null)
            imagesView.getSelectionModel().select(activeImage);
        else
            imagesView.getSelectionModel().clearSelection();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        boolean listVisible = getChildren().contains(imagesView);

        if (listVisible && !selected)
            getChildren().remove(imagesView);
        else if (!listVisible && selected)
            getChildren().add(imagesView);
    }
}

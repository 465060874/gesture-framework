package io.github.samwright.framework.javacv.viewers;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.TaggedImage;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 17:40
 */
public class ImageViewer extends DataViewer {

    private ImageView imageView = new ImageView();
    private Label tag = new Label();

    public ImageViewer() {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        getChildren().add(imageView);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        imageView.setFitWidth(140);


        getChildren().add(tag);
        tag.setTranslateX(0);
        tag.translateYProperty().bind(imageView.fitHeightProperty());

        minWidthProperty().bind(Bindings.max(imageView.fitWidthProperty(), tag.widthProperty()));
        minHeightProperty().bind(imageView.fitHeightProperty().add(tag.heightProperty()));
    }

    @Override
    public DataViewer createClone() {
        return new ImageViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return TaggedImage.class;
    }

    @Override
    public void view(Mediator mediator) {
        TaggedImage image = (TaggedImage) mediator.getData();
        opencv_core.IplImage iplImage = image.getImage();
        view(iplImage);
        if (image.getTag() == null)
            tag.setText("");
        else
            tag.setText(image.getTag());
    }

    public void view(opencv_core.IplImage image) {
        try {
            imageView.setImage(Image.impl_fromExternalImage(image.getBufferedImage()));
        } catch (Exception e) {
            MainWindowController.getTopController().handleException(e);
        }
    }

    @Override
    public String toString() {
        return "OpenCV Image";
    }
}

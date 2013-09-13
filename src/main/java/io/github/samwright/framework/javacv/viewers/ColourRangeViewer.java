package io.github.samwright.framework.javacv.viewers;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.ColourRange;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * User: Sam Wright Date: 08/09/2013 Time: 23:27
 */
public class ColourRangeViewer extends DataViewer {

    private Rectangle rect1, rect2;

    public ColourRangeViewer() {
        rect1 = new Rectangle(0, 0, 50, 50);
        rect2 = new Rectangle(50, 0, 50, 50);

        getChildren().addAll(rect1, rect2);

        setMinWidth(100);
        setMinHeight(50);
    }

    @Override
    public DataViewer createClone() {
        return new ColourRangeViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return ColourRange.class;
    }

    @Override
    public void view(Mediator mediator) {
        ColourRange colourRange = (ColourRange) mediator.getData();
        rect1.setFill(getColour(colourRange.getLowerThreshold()));
        rect2.setFill(getColour(colourRange.getUpperThreshold()));
    }

    private Color getColour(opencv_core.CvScalar cvScalar) {
        return Color.hsb(cvScalar.blue() * 1./256, cvScalar.green() * 1./256,
                cvScalar.red() *1./256);

    }

    @Override
    public String toString() {
        return "Colour Range";
    }
}

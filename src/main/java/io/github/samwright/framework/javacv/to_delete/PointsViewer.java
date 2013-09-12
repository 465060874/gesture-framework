package io.github.samwright.framework.javacv.to_delete;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.ImageViewer;
import io.github.samwright.framework.model.helper.Mediator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 09:21
 */
public class PointsViewer extends ImageViewer {
    @Override
    public DataViewer createClone() {
        return new PointsViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Points.class;
    }

    @Override
    public void view(Mediator mediator) {
        Points points = (Points) mediator.getData();
        opencv_core.IplImage src = points.getSourceImage();

        opencv_core.IplImage copiedImage = opencv_core.IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        for (opencv_core.CvPoint point : points.getPoints())
            drawCircle(copiedImage, opencv_core.CvScalar.RED, point);

        super.view(copiedImage);
    }

    private void drawCircle(opencv_core.IplImage image, opencv_core.CvScalar colour, opencv_core.CvPoint position) {
        cvDrawCircle(image, position, 10, colour, 10, CV_AA, 0);
    }

    @Override
    public String toString() {
        return "Points Viewer";
    }
}

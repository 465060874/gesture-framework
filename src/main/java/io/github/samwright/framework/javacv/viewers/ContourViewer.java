package io.github.samwright.framework.javacv.viewers;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.model.helper.Mediator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 18:26
 */
public class ContourViewer extends ImageViewer {

    @Override
    public DataViewer createClone() {
        return new ContourViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Contour.class;
    }

    @Override
    public void view(Mediator mediator) {
        Contour contours = (Contour) mediator.getData();
        IplImage src = contours.getSourceTaggedImage().getImage();

        opencv_core.IplImage copiedImage = IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        opencv_core.CvScalar colour = opencv_core.CvScalar.RED;
        cvDrawContours(copiedImage, contours.getContour(), colour, colour, -1, 3, CV_AA);

        super.view(copiedImage);
    }

    @Override
    public String toString() {
        return "Contour Viewer";
    }
}

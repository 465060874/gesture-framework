package io.github.samwright.framework.javacv;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.Palm;
import io.github.samwright.framework.model.helper.Mediator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 16:56
 */
public class PalmViewer extends ImageViewer {
    @Override
    public DataViewer createClone() {
        return new PalmViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Palm.class;
    }

    @Override
    public void view(Mediator mediator) {
        Palm palm = (Palm) mediator.getData();
        opencv_core.IplImage src = palm.getSourceTaggedImage().getImage();

        opencv_core.IplImage copiedImage = opencv_core.IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        opencv_core.CvScalar colour = opencv_core.CvScalar.RED;
        cvDrawCircle(copiedImage, palm.getCog(), 10, colour, 10, CV_AA, 0);

        int lineLength = 200;
        int deltaX = (int) (lineLength * Math.cos(palm.getContourAxisAngle()));
        int deltaY = (int) (lineLength * Math.sin(palm.getContourAxisAngle()));
        CvPoint endPoint = new CvPoint(palm.getCog().x() + deltaX, palm.getCog().y() + deltaY);

        cvDrawLine(copiedImage, palm.getCog(), endPoint, colour, 5, CV_AA, 0);

        super.view(copiedImage);
    }

    @Override
    public String toString() {
        return "Palm Viewer";
    }
}

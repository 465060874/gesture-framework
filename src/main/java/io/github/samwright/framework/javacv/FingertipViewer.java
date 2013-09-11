package io.github.samwright.framework.javacv;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.Fingertips;
import io.github.samwright.framework.model.helper.Mediator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 17:29
 */
public class FingertipViewer extends ImageViewer {

    @Override
    public DataViewer createClone() {
        return new FingertipViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Fingertips.class;
    }

    @Override
    public void view(Mediator mediator) {
        Fingertips fingertips = (Fingertips) mediator.getData();
        opencv_core.IplImage src = fingertips.getSourceImage();

        opencv_core.IplImage copiedImage = opencv_core.IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        for (CvPoint tip : fingertips.getTips())
            drawCircle(copiedImage, CvScalar.RED, tip);

        for (CvPoint fold : fingertips.getFolds())
            drawCircle(copiedImage, CvScalar.GREEN, fold);

        super.view(copiedImage);
    }

    private void drawCircle(IplImage image, CvScalar colour, CvPoint position) {
        cvDrawCircle(image, position, 10, colour, 10, CV_AA, 0);
    }

    @Override
    public String toString() {
        return "Palm Viewer";
    }

}

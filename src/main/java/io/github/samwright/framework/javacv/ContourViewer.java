package io.github.samwright.framework.javacv;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.Contours;
import io.github.samwright.framework.javacv.helper.TaggedImage;
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
        return Contours.class;
    }

    @Override
    public void view(Mediator mediator) {
        Contours contours = (Contours) mediator.getData();
        do {
            mediator = mediator.getPrevious();
        } while(!(mediator.getData() instanceof TaggedImage));
        TaggedImage image = (TaggedImage) mediator.getData();
        IplImage src = image.getImage();

        opencv_core.IplImage copiedImage = IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        opencv_core.CvScalar colour = opencv_core.CvScalar.RED;
        for (opencv_core.CvSeq contour : contours.getContours())
            cvDrawContours(copiedImage, contour, colour, colour, -1, 3, CV_AA);

        super.view(copiedImage);
    }

    @Override
    public String toString() {
        return "Contour Viewer";
    }
}

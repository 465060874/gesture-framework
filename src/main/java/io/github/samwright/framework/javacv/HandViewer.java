package io.github.samwright.framework.javacv;

import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.javacv.helper.Hand;
import io.github.samwright.framework.model.helper.Mediator;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 11:47
 */
public class HandViewer extends ImageViewer {
    @Override
    public DataViewer createClone() {
        return new HandViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Hand.class;
    }

    @Override
    public void view(Mediator mediator) {
        Hand hand = (Hand) mediator.getData();
        opencv_core.IplImage src = hand.getSourceTaggedImage().getImage();

        opencv_core.IplImage copiedImage = opencv_core.IplImage.create(cvGetSize(src), src.depth(), 3);
        cvCvtColor(src, copiedImage, CV_GRAY2BGR);

        CvPoint centre = hand.getPalm().getCog();

        System.out.println("\n== Fingers:");

        for (int i = 0; i < hand.getDigitAngles().size(); ++i) {
            double angle = hand.getDigitAngles().get(i);
            double length = hand.getDigitLengths().get(i);
            System.out.format("angle: %f , length: %f%n", angle, length);

            CvPoint fingertip = new CvPoint(
                    (int) (centre.x() + length * Math.cos(angle)),
                    (int) (centre.y() + length * Math.sin(angle))
            );
            cvDrawLine(copiedImage, centre, fingertip, CvScalar.RED, 10, CV_AA, 0);
        }

        CvPoint dirPnt = new CvPoint(
                (int) (centre.x() + hand.getWidth() * Math.cos(hand.getPalm().getContourAxisAngle())),
                (int) (centre.y() + hand.getWidth() * Math.sin(hand.getPalm().getContourAxisAngle()))
        );
        cvDrawLine(copiedImage, centre, dirPnt, CvScalar.GREEN, 10, CV_AA, 0);


        super.view(copiedImage);
    }

    private void drawCircle(opencv_core.IplImage image, opencv_core.CvScalar colour, opencv_core.CvPoint position) {
        cvDrawCircle(image, position, 10, colour, 10, CV_AA, 0);
    }

    @Override
    public String toString() {
        return "Fingertip Viewer";
    }
}

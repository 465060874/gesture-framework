package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.javacv.helper.Palm;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import static com.googlecode.javacv.cpp.opencv_core.CvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 16:39
 */
public class PalmDetector extends AbstractElement {


    public PalmDetector() {
        super(new TypeData(Contour.class, Palm.class));
    }

    public PalmDetector(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Contour contour = (Contour) input.getData();

        // adapted from: http://www.javacodegeeks.com/2012/12/hand-and-finger-detection-using-javacv.html

        CvPoint cogPt = null;
        int contourAxisAngle;
        CvMoments moments = new CvMoments();
        cvMoments(contour.getContour(), moments, 1);

        // center of gravity
        double m00 = cvGetSpatialMoment(moments, 0, 0);
        double m10 = cvGetSpatialMoment(moments, 1, 0);
        double m01 = cvGetSpatialMoment(moments, 0, 1);

        if (m00 != 0) {   // calculate center
            int xCenter = (int) Math.round(m10 / m00);
            int yCenter = (int) Math.round(m01 / m00);
            cogPt = new CvPoint(xCenter, yCenter);
        }

        double m11 = cvGetCentralMoment(moments, 1, 1);
        double m20 = cvGetCentralMoment(moments, 2, 0);
        double m02 = cvGetCentralMoment(moments, 0, 2);
        contourAxisAngle = calculateTilt(m11, m20, m02);

        return input.createNext(this, new Palm(cogPt, contourAxisAngle, contour.getSourceImage()));
    }

    private int calculateTilt(double m11, double m20, double m02) {
        double diff = m20 - m02;
        if (diff == 0) {
            if (m11 == 0)
                return 0;
            else if (m11 > 0)
                return 45;
            else   // m11 < 0
                return -45;
        }

        double theta = 0.5 * Math.atan2(2 * m11, diff);
        int tilt = (int) Math.round(Math.toDegrees(theta));

        if ((diff > 0) && (m11 == 0))
            return 0;
        else if ((diff < 0) && (m11 == 0))
            return -90;
        else if ((diff > 0) && (m11 > 0))  // 0 to 45 degrees
            return tilt;
        else if ((diff > 0) && (m11 < 0))  // -45 to 0
            return (180 + tilt);   // change to counter-clockwise angle
        else if ((diff < 0) && (m11 > 0))   // 45 to 90
            return tilt;
        else if ((diff < 0) && (m11 < 0))   // -90 to -45
            return (180 + tilt);  // change to counter-clockwise angle

        System.out.println("Error in moments for tilt angle");
        return 0;
    }

    @Override
    public Element createMutableClone() {
        return new PalmDetector(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

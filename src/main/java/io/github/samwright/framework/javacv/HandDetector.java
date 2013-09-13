package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.javacv.helper.Fingertips;
import io.github.samwright.framework.javacv.helper.Hand;
import io.github.samwright.framework.javacv.helper.Palm;
import io.github.samwright.framework.model.SplitJoinWorkflowContainer;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;


/**
 * User: Sam Wright Date: 11/09/2013 Time: 14:11
 *
 * Adapted from: http://www.javacodegeeks.com/2012/12/hand-and-finger-detection-using-javacv.html
 */
public class HandDetector extends SplitJoinWorkflowContainer {

    public HandDetector() {
        super(new TypeData(Contour.class, Hand.class),
                Arrays.asList(
                        new TypeData(Contour.class, Contour.class),
                        new TypeData(Contour.class, Palm.class), // Centroid + angle
                        new TypeData(Contour.class, Fingertips.class) // Fingertip points
                ));
    }

    public HandDetector(SplitJoinWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public SplitJoinWorkflowContainer createMutableClone() {
        return new HandDetector(this);
    }

    @Override
    public Object joinOutputMediators(List<Mediator> mediators) {
        Contour contour = (Contour) mediators.get(0).getData();
        Palm palm = (Palm) mediators.get(1).getData();
        Fingertips fingertips = (Fingertips) mediators.get(2).getData();

//        CvSeq cvSeq = new CvSeq();
//        int i = 0;
//        for (CvPoint point : fingertips.getFolds())
//            cvSeqInsert(cvSeq, i++, point);
//
        CvMemStorage storage = CvMemStorage.create();
//
        CvBox2D cvRect = cvMinAreaRect2(contour.getContour(), storage);
////        CvBox2D cvRect = cvMinAreaRect2(cvSeq, storage);
//
        CvSize2D32f size = cvRect.size();
//        CvPoint centre = cvPointFrom32f(cvRect.center());


//        System.out.format("Angle: %f , Width: %f , Height: %f%n", cvRect.angle(), size.width(),
//                size.height());

        float angle = (cvRect.angle() + 225) % 180 - 45;
        double width, height;

        if (angle < 45) {
            // top edge is a width
            width = size.width();
            height = size.height();
        } else {
            // top edge is a height
            width = size.height();
            height = size.width();
            angle = angle - 90;
        }
//        System.out.format(" == Angle: %f , Width: %f , Height: %f%n", angle, width, height);
//
//                // Angle is now in range -45 to 45.
//        // top edge is a width
//
//        double topLeftX = centre.x()
//                - width * Math.cos(angle) * 0.5
//                + height * Math.sin(angle) * 0.5;
//
//        double topRightX = centre.x()
//                + width * Math.cos(angle) * 0.5
//                + height * Math.sin(angle) * 0.5;
//
//        double topLeftY = centre.y()
//                + height * Math.cos(angle) * 0.5
//                + width * Math.sin(angle) * 0.5;
//
//        double topRightY = centre.y()
//                + height * Math.cos(angle) * 0.5
//                - width * Math.sin(angle) * 0.5;
//
//        CvPoint topLeft = new CvPoint((int)topLeftX, (int)topLeftY);
//        CvPoint topRight = new CvPoint((int) topRightX, (int) topRightY);
//
//

//        int thumbFingerMin = 80;
//        double fingerFingerMax = width / 4.;

//        System.out.println(" === ANGLE: " + cvRect.angle() / 180 * Math.PI);
        Palm newPalm = new Palm(palm.getCog(), cvRect.angle() / 180 * Math.PI,
                palm.getSourceTaggedImage());


        List<Double> digitAngles = new ArrayList<>();
        List<Double> digitLengths = new ArrayList<>();

        for (CvPoint tip : fingertips.getTips()) {
            digitAngles.add(angleToHorizontal(palm.getCog(), tip));
            digitLengths.add(distanceBetween(palm.getCog(), tip));
        }

//        for (int i = 0; i < 5; ++i)
//            digits.set(i, -1);
//
//        digits.set(0,0);
//        if ()
//
//        CvPoint previousTip = fingertips.getTips().get(0);
//        int currentIndex = 1;
//        for (CvPoint tip : fingertips.getTips()) {
//            if (previousTip != null) {
//                double distance = distanceBetween(tip, previousTip);
//                if (distance < fingerFingerMax) {
//                    digits.set(currentIndex, )
//                }
//
//            } else {
//                digits.set(currentIndex, currentIndex);
//            }
//
//            ++currentIndex;
//            previousTip = tip;
//        }

        int[] digitIndices = new int[5];
        for (int i = 0; i < 5; ++i)
            digitIndices[i] = -1;
//
//        double[] idealAngles = new double[]{-2.8, -0.8, };
//        double[] angleDeltas = new double[]{};

        // Find thumb - most negative angle
//        double maxAngle = -1000;
//        int thumbIndex = 0;
//        for (int i = 0; i < digitAngles.size(); ++i) {
//            if (maxAngle < digitAngles.get(i)) {
//                maxAngle = digitAngles.get(i);
//                thumbIndex = i;
//            }
//        }

        List<Double> orderedAngles = new ArrayList<>(digitAngles);
        Collections.sort(orderedAngles);
        // Use first as thumb, unless it's too close to the next digit
//        if (Math.abs(orderedAngles.get(0) - orderedAngles.get(1)) > minThumbSeparation)
//            digitIndices[0] = digitAngles.indexOf(orderedAngles.get(0));

        for (int i = 0; i < orderedAngles.size() && i < 5; ++i)
            digitIndices[i] = digitAngles.indexOf(orderedAngles.get(i));

        List<Double> finalDigitAngles = new ArrayList<>();
        List<Double> finalDigitLengths = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            if (digitIndices[i] == -1) {
                finalDigitAngles.add(newPalm.getContourAxisAngle());
                finalDigitLengths.add(width / 5);
            } else {
                finalDigitAngles.add(digitAngles.get(digitIndices[i]));
                finalDigitLengths.add(digitLengths.get(digitIndices[i]));
            }
        }

        return new Hand(newPalm, finalDigitLengths, finalDigitAngles, contour.getSourceTaggedImage(),
                width);
    }

    private double distanceBetween(CvPoint a, CvPoint b) {
        return Math.sqrt(Math.pow(a.x() - b.x(),2) + Math.pow(a.y() - b.y(), 2));
    }

    private double angleToHorizontal(CvPoint a, CvPoint b) {
        double xDelta = b.x() - a.x();
        double yDelta = b.y() - a.y();
        return Math.atan2(yDelta, xDelta);
    }
}

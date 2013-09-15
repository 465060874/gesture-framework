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

        CvMemStorage storage = CvMemStorage.create();
        CvBox2D cvRect = cvMinAreaRect2(contour.getContour(), storage);
        CvSize2D32f size = cvRect.size();

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

        Palm newPalm = new Palm(palm.getCog(), cvRect.angle() / 180 * Math.PI,
                palm.getSourceTaggedImage());


        List<Double> digitAngles = new ArrayList<>();
        List<Double> digitLengths = new ArrayList<>();

        for (CvPoint tip : fingertips.getTips()) {
            digitAngles.add(angleToHorizontal(palm.getCog(), tip));
            digitLengths.add(distanceBetween(palm.getCog(), tip));
        }

        int[] digitIndices = new int[5];
        for (int i = 0; i < 5; ++i)
            digitIndices[i] = -1;

        List<Double> orderedAngles = new ArrayList<>(digitAngles);
        Collections.sort(orderedAngles);

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

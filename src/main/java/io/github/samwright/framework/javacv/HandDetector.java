package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.javacv.helper.ConvexityDefects;
import io.github.samwright.framework.javacv.helper.Fingertips;
import io.github.samwright.framework.javacv.helper.Palm;
import io.github.samwright.framework.model.SplitJoinWorkflowContainer;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.Arrays;
import java.util.List;


/**
 * User: Sam Wright Date: 11/09/2013 Time: 14:11
 *
 * Adapted from: http://www.javacodegeeks.com/2012/12/hand-and-finger-detection-using-javacv.html
 */
public class HandDetector extends SplitJoinWorkflowContainer {

    public HandDetector() {
        super(new TypeData(Contour.class, ConvexityDefects.class),
                Arrays.asList(
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
        Contour contour, convexHull;
        try {
            contour = (Contour) mediators.get(0).getData();
            convexHull = (Contour) mediators.get(1).getData();
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }

        // Find contour defects
////        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
////        opencv_core.CvSeq defects = cvConvexityDefects(
////                contour.getContours().get(0),
////                convexHull.getContoursWithPoints().get(0),
////                storage
////        );
//
//        int i = 0;
//        List<CvConvexityDefect> defectsList = new ArrayList<>();
//        while(true) {
//            Pointer pntr;
//            pntr = cvGetSeqElem(defects, i);
//            if (pntr == null || pntr.isNull())
//                break;
//            defectsList.add(new CvConvexityDefect(pntr));
//            ++i;
//        }
//
//        for (CvConvexityDefect defect : defectsList) {
//            defect.
//        }

        return null;
    }
}

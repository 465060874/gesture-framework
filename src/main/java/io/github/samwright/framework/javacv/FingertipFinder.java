package io.github.samwright.framework.javacv;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.javacv.helper.Fingertips;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 17:14
 */
public class FingertipFinder extends AbstractElement {

    public FingertipFinder() {
        super(new TypeData(Contour.class, Fingertips.class));
    }

    public FingertipFinder(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Contour contour = (Contour) input.getData();

        CvMemStorage storage = CvMemStorage.create();

        CvSeq approxContour = cvApproxPoly(contour.getContour(),
                Loader.sizeof(CvContour.class),
                storage, CV_POLY_APPROX_DP, 3, 1);
        // reduce number of points in the contour

        CvSeq hullSeq = cvConvexHull2(approxContour,
                storage, CV_COUNTER_CLOCKWISE, 0);
        // find the convex hull around the contour

        CvSeq defects = cvConvexityDefects(approxContour,
                hullSeq, storage);
        // find the defect differences between the contour and hull

        List<CvPoint> tips = new ArrayList<>();
        List<CvPoint> folds = new ArrayList<>();
        List<Float> depths = new ArrayList<>();

        // copy defect information from defects sequence into arrays
        for (int i = 0; i < defects.total(); i++) {
            Pointer pntr = cvGetSeqElem(defects, i);
            CvConvexityDefect defect = new CvConvexityDefect(pntr);

            CvPoint startPt = defect.start();

            tips.add(new CvPoint(Math.round(startPt.x()), Math.round(startPt.y())));
            // array contains coords of the fingertips

            CvPoint endPt = defect.end();
            CvPoint depthPt = defect.depth_point();
            folds.add(new CvPoint(Math.round(depthPt.x()), Math.round(depthPt.y())));
            //array contains coords of the skin fold between fingers

            depths.add(defect.depth());
            // array contains distances from tips to folds
        }

        return input.createNext(this, new Fingertips(tips, folds, depths,contour.getSourceImage()));
    }

    @Override
    public Element createMutableClone() {
        return new FingertipFinder(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

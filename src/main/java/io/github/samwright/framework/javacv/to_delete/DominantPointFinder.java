package io.github.samwright.framework.javacv.to_delete;

import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_legacy.CV_DOMINANT_IPAN;
import static com.googlecode.javacv.cpp.opencv_legacy.cvFindDominantPoints;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 09:20
 */
public class DominantPointFinder extends AbstractElement {

    public DominantPointFinder() {
        super(new TypeData(Contour.class, Points.class));
    }

    public DominantPointFinder(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Contour contour = (Contour) input.getData();
        CvMemStorage storage = CvMemStorage.create();
        CvSeq dominantPoints = cvFindDominantPoints(contour.getContour(), storage, CV_DOMINANT_IPAN,
                7, 9, 9, 150);

        List<CvPoint> points = new ArrayList<>();
        for (int i = 0; i < dominantPoints.total(); i++) {
            IntPointer pntr = (IntPointer) cvGetSeqElem(dominantPoints, i);
            Pointer pointPntr = cvGetSeqElem(contour.getContour(), pntr.get());
            points.add(new CvPoint(pointPntr));
        }

        return input.createNext(this, new Points(points, contour.getSourceTaggedImage().getImage()));
    }

    @Override
    public Element createMutableClone() {
        return new DominantPointFinder(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

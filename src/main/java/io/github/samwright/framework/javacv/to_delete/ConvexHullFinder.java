package io.github.samwright.framework.javacv.to_delete;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexHull2;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 13:32
 */
public class ConvexHullFinder extends AbstractElement {

    public ConvexHullFinder() {
        super(new TypeData(Contour.class, Contour.class));
    }

    public ConvexHullFinder(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        Contour contour = (Contour) input.getData();

        Pointer storage = opencv_core.CvMemStorage.create();
        opencv_core.CvSeq cvSeq = cvConvexHull2(contour.getContour(), storage,
                CV_CLOCKWISE, 1);
        opencv_core.CvSeq cvSeqWithPoints = cvConvexHull2(contour.getContour(), storage,
                CV_CLOCKWISE, 0);

        List<opencv_core.CvSeq> cvSeqs = Contour.cvSeq2List(cvSeq, 0, 10000);
        List<opencv_core.CvSeq> cvSeqsWithPoints = Contour.cvSeq2List(cvSeqWithPoints, 0, 10000);

//        return input.createNext(this, new Contour(cvSeqs, contour.getSourceImage()));
        return null;
    }

    @Override
    public Element createMutableClone() {
        return new ConvexHullFinder(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

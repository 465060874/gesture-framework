package io.github.samwright.framework.javacv;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import io.github.samwright.framework.javacv.helper.Contours;
import io.github.samwright.framework.javacv.helper.TaggedImage;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 15:13
 */
public class ContourFinder extends AbstractElement {

    @Getter private int upperLimit, lowerLimit;

    public ContourFinder() {
        super(new TypeData(TaggedImage.class, Contours.class));
        upperLimit = 1000;
        lowerLimit = 100;
    }

    public ContourFinder(ContourFinder oldElement) {
        super(oldElement);
        upperLimit = oldElement.getUpperLimit();
        lowerLimit = oldElement.getLowerLimit();
    }

    @Override
    public Mediator process(Mediator input) {
        TaggedImage image = (TaggedImage) input.getData();
        opencv_core.IplImage src = cvCloneImage(image.getImage());

        CvMemStorage storage = CvMemStorage.create();
        CvSeq contourSeq = new CvSeq(null);
        cvFindContours(src, storage, contourSeq, Loader.sizeof(opencv_core.CvContour.class),
                CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

        // Convert contour sequence to array list
        CvSeq contour = contourSeq;
        List<CvSeq> contours = new ArrayList<>();
        while (contour != null && !contour.isNull()) {
            if (contour.elem_size() > 0
                    && contour.total() > lowerLimit
                    && contour.total() > upperLimit) {
                contours.add(contour);
            }
            contour = contour.h_next();
        }

        return input.createNext(this, new Contours(contours));
    }

    @Override
    public ContourFinder createMutableClone() {
        return new ContourFinder(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public ContourFinder withUpperLimit(int upperLimit) {
        if (isMutable()) {
            this.upperLimit = upperLimit;
            return this;
        } else {
            return createMutableClone().withUpperLimit(upperLimit);
        }
    }

    public ContourFinder withLowerLimit(int lowerLimit) {
        if (isMutable()) {
            this.lowerLimit = lowerLimit;
            return this;
        } else {
            return createMutableClone().withLowerLimit(lowerLimit);
        }
    }
}

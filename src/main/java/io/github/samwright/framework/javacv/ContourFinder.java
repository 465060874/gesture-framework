package io.github.samwright.framework.javacv;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize2D32f;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.javacv.helper.TaggedImage;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.UUID;

import static com.googlecode.javacv.cpp.opencv_core.cvCloneImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 15:13
 */
public class ContourFinder extends AbstractElement {

    @Getter private int upperLimit, lowerLimit;

    public ContourFinder() {
        super(new TypeData(TaggedImage.class, Contour.class));
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
        CvSeq contours = new CvSeq(null);
        cvFindContours(src, storage, contours, Loader.sizeof(opencv_core.CvContour.class),
                CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);
//        storage.release();

        // adapted from: http://www.javacodegeeks.com/2012/12/hand-and-finger-detection-using-javacv.html
        // find the largest contour in the list based on bounded box size
        float maxArea = 200;
        CvBox2D maxBox = null;
        CvSeq bigContour = null;

        while (contours != null && !contours.isNull()) {
            if (contours.elem_size() > 0) {
                CvBox2D box = cvMinAreaRect2(contours, storage);
                if (box != null) {
                    CvSize2D32f size = box.size();
                    float area = size.width() * size.height();
                    if (area > maxArea) {
                        maxArea = area;
                        bigContour = contours;
                    }
                }
            }
            contours = contours.h_next();
        }

        return input.createNext(this, new Contour(bigContour, image));
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

    @Override
    public ContourFinder withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return createMutableClone().withXML(node, map);

        super.withXML(node, map);
        upperLimit = Integer.parseInt(XMLHelper.getDataUnderNode(node, "UpperLimit"));
        lowerLimit = Integer.parseInt(XMLHelper.getDataUnderNode(node, "LowerLimit"));

        return this;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);

        XMLHelper.addDataUnderNode(node, "UpperLimit", String.valueOf(upperLimit));
        XMLHelper.addDataUnderNode(node, "LowerLimit", String.valueOf(lowerLimit));

        return node;
    }
}

package io.github.samwright.framework.javacv;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.javacv.helper.Contour;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.UUID;

import static com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 20:15
 */
public class SimplifyContour extends AbstractElement {

    @Getter private double accuracy;


    public SimplifyContour() {
        super(new TypeData(Contour.class, Contour.class));
        accuracy = 3.0;

    }

    public SimplifyContour(SimplifyContour oldElement) {
        super(oldElement);
        this.accuracy = oldElement.getAccuracy();

    }

    @Override
    public Mediator process(Mediator input) {
        Contour contour = (Contour) input.getData();
        CvMemStorage storage = CvMemStorage.create();
        CvSeq approxContour = cvApproxPoly(contour.getContour(),
                Loader.sizeof(opencv_core.CvContour.class),
                storage, CV_POLY_APPROX_DP, accuracy, 1);

        return input.createNext(this, new Contour(approxContour, contour.getSourceTaggedImage()));
    }

    @Override
    public SimplifyContour createMutableClone() {
        return new SimplifyContour(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        XMLHelper.addDataUnderNode(node, "Accuracy", String.valueOf(accuracy));
        return node;
    }

    @Override
    public Element withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return createMutableClone().withXML(node, map);

        super.withXML(node, map);
        accuracy = Double.parseDouble(XMLHelper.getDataUnderNode(node, "Accuracy"));
        return this;
    }

    public SimplifyContour withAccuracy(double accuracy) {
        if (isMutable()) {
            this.accuracy = accuracy;
            return this;
        } else {
            return createMutableClone().withAccuracy(accuracy);
        }
    }
}

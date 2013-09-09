package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.ColourRange;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import org.w3c.dom.Document;

import java.util.*;

import static com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvScalar;


/**
 * User: Sam Wright Date: 08/09/2013 Time: 23:20
 */
public class StaticColourRange extends AbstractElement {

    @Getter private List<Double> upperRange, lowerRange;

    public StaticColourRange() {
        super(new TypeData(TaggedImage.class, ColourRange.class));
        upperRange = Arrays.asList(0., 40., 89.);
        lowerRange = Arrays.asList(25., 173., 229.);
    }

    public StaticColourRange(StaticColourRange oldElement) {
        super(oldElement);
        upperRange = new ArrayList<>(oldElement.getUpperRange());
        lowerRange = new ArrayList<>(oldElement.getLowerRange());
    }

    public StaticColourRange withRanges(List<Double> upperRange, List<Double> lowerRange) {
        if (isMutable()) {
            this.upperRange = upperRange;
            this.lowerRange = lowerRange;
            return this;
        } else {
            return createMutableClone().withRanges(upperRange, lowerRange);
        }
    }

    @Override
    public Mediator process(Mediator input) {
//        TaggedImage image = (TaggedImage) input.getData();
//        IplImage src = image.getImage();
//
//        CvHaarClassifierCascade cascade = new
//                CvHaarClassifierCascade(cvLoad("/haar/haarcascade_frontalface_default.xml"));
//        CvMemStorage storage = CvMemStorage.create();
//        CvSeq sign = cvHaarDetectObjects(
//                src,
//                cascade,
//                storage,
//                1.5,
//                3,
//                CV_HAAR_DO_CANNY_PRUNING);
//
//        cvClearMemStorage(storage);
//
//        int total_Faces = sign.total();
//
//        for (int i = 0; i < total_Faces; i++) {
//            CvRect r = new CvRect(cvGetSeqElem(sign, i));
//
//            // Create region of interest on face
//            IplROI roi = new IplROI();
//            roi.xOffset(r.x());
//            roi.yOffset(r.y());
//            roi.width(r.width());
//            roi.height(r.height());
//            src.roi(roi);
//
//            // Create histogram from ROI
//            hist = cvCreateHist(2, hist_size, CV_HIST_ARRAY, ranges, 1);
//
//        }

//        ColourRange colourRange = new ColourRange(cvScalar(0, 40, 89, 0), cvScalar(25, 173, 229,0));
        ColourRange colourRange
                = new ColourRange(computeRange(lowerRange),computeRange(upperRange));

        return input.createNext(this, colourRange);
    }

    private CvScalar computeRange(List<Double> range) {
        return cvScalar(range.get(0), range.get(1), range.get(2), 0);
    }

    @Override
    public StaticColourRange createMutableClone() {
        return new StaticColourRange(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        org.w3c.dom.Element upperLimitNode = doc.createElement("UpperLimit");
        org.w3c.dom.Element lowerLimitNode = doc.createElement("LowerLimit");

        XMLHelper.addDataUnderNode(upperLimitNode, "H", upperRange.get(0).toString());
        XMLHelper.addDataUnderNode(upperLimitNode, "S", upperRange.get(1).toString());
        XMLHelper.addDataUnderNode(upperLimitNode, "V", upperRange.get(2).toString());

        XMLHelper.addDataUnderNode(lowerLimitNode, "H", lowerRange.get(0).toString());
        XMLHelper.addDataUnderNode(lowerLimitNode, "S", lowerRange.get(1).toString());
        XMLHelper.addDataUnderNode(lowerLimitNode, "V", lowerRange.get(2).toString());

        node.appendChild(upperLimitNode);
        node.appendChild(lowerLimitNode);

        return node;
    }

    @Override
    public StaticColourRange withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return createMutableClone().withXML(node, map);

        super.withXML(node, map);
        upperRange.clear();
        lowerRange.clear();

        org.w3c.dom.Element upperLimitNode = XMLHelper.getFirstChildWithName(node, "UpperLimit");
        org.w3c.dom.Element lowerLimitNode = XMLHelper.getFirstChildWithName(node, "LowerLimit");

        upperRange.add(Double.valueOf(XMLHelper.getDataUnderNode(upperLimitNode, "H")));
        upperRange.add(Double.valueOf(XMLHelper.getDataUnderNode(upperLimitNode, "S")));
        upperRange.add(Double.valueOf(XMLHelper.getDataUnderNode(upperLimitNode, "V")));

        lowerRange.add(Double.valueOf(XMLHelper.getDataUnderNode(lowerLimitNode, "H")));
        lowerRange.add(Double.valueOf(XMLHelper.getDataUnderNode(lowerLimitNode, "S")));
        lowerRange.add(Double.valueOf(XMLHelper.getDataUnderNode(lowerLimitNode, "V")));

        return this;
    }
}

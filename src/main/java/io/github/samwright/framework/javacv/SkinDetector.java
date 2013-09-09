package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.ColourRange;
import io.github.samwright.framework.model.SplitJoinWorkflowContainer;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.Arrays;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 22:09
 */
public class SkinDetector extends SplitJoinWorkflowContainer {

    public SkinDetector() {
        super(new TypeData(TaggedImage.class, TaggedImage.class),
                Arrays.asList(
                        new TypeData(TaggedImage.class, TaggedImage.class),
                        new TypeData(TaggedImage.class, ColourRange.class)));
    }

    public SkinDetector(SplitJoinWorkflowContainer oldElement) {
        super(oldElement);
    }

    @Override
    public Object joinOutputMediators(List<Mediator> mediators) {
        TaggedImage taggedImage;
        ColourRange colourRange;
        try {
            taggedImage = (TaggedImage) mediators.get(0).getData();
            colourRange = (ColourRange) mediators.get(1).getData();
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }

        IplImage image = taggedImage.getImage();

        IplImage hsvImage = image.clone();
        cvCvtColor(image, hsvImage, CV_BGR2HSV);

        IplImage bwImage = cvCreateImage(cvGetSize(hsvImage), 8, 1);
//        cvInRangeS(hsvImage, cvScalar(0, 58, 89, 0), cvScalar(25, 173, 229, 0), bwImage);
        cvInRangeS(hsvImage, colourRange.getLowerThreshold(), colourRange.getUpperThreshold(), bwImage);
        cvSmooth(bwImage, bwImage, CV_MEDIAN, 13);


        return new TaggedImage(bwImage, taggedImage.getTag());
    }

    @Override
    public SkinDetector createMutableClone() {
        return new SkinDetector(this);
    }
}

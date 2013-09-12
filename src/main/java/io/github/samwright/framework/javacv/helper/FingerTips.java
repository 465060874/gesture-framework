package io.github.samwright.framework.javacv.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.CvPoint;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 16:30
 */
@AllArgsConstructor
public class Fingertips {
    @Getter private final List<CvPoint> tips;
    @Getter private final List<CvPoint> folds;
    @Getter private final List<Float> depths;
    @Getter private final TaggedImage sourceTaggedImage;
}

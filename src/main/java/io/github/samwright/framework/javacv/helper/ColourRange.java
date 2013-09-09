package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: Sam Wright Date: 08/09/2013 Time: 17:39
 */
@AllArgsConstructor
public class ColourRange {
    @Getter private final opencv_core.CvScalar lowerThreshold, upperThreshold;
}

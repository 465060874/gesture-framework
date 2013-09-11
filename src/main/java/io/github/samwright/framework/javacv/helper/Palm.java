package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 16:37
 */
@AllArgsConstructor
public class Palm {
    @Getter private final opencv_core.CvPoint cog;
    @Getter private final int contourAxisAngle;
    @Getter private final opencv_core.IplImage sourceImage;
}

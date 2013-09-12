package io.github.samwright.framework.javacv.to_delete;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 09:20
 */
@AllArgsConstructor
public class Points {
    @Getter private final List<opencv_core.CvPoint> points;
    @Getter private final opencv_core.IplImage sourceImage;
}

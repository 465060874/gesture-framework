package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 18:12
 */
@AllArgsConstructor
public class Contours {
    @Getter private List<opencv_core.CvSeq> contours;
}

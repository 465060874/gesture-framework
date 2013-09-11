package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Wright Date: 10/09/2013 Time: 18:12
 */
@AllArgsConstructor
public class Contour {
    @Getter private final opencv_core.CvSeq contour;
    @Getter private final opencv_core.IplImage sourceImage;

    public static List<opencv_core.CvSeq> cvSeq2List(opencv_core.CvSeq seq, int lowerLimit,
                                                     int upperLimit) {
        List<opencv_core.CvSeq> contours = new ArrayList<>();
        while (seq != null && !seq.isNull()) {
            if (seq.elem_size() > 0
                    && (lowerLimit == -1 || seq.total() >= lowerLimit)
                    && (upperLimit == -1 || seq.total() <= upperLimit)) {
                contours.add(seq);
            }
            seq = seq.h_next();
        }

        return contours;
    }
}

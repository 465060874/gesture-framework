package io.github.samwright.framework.javacv.helper;

import io.github.samwright.framework.model.datatypes.Features;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 09:05
 */
@AllArgsConstructor
public class Hand implements Features {
    @Getter private final Palm palm;
    @Getter private final List<Double> digitLengths, digitAngles;
    @Getter private final TaggedImage sourceTaggedImage;
    @Getter private final double width;

    @Override
    public List<Double> getFeatures() {
        return digitLengths;
    }

    @Override
    public String getTag() {
        return sourceTaggedImage.getTag();
    }
}

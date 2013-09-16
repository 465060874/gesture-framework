package io.github.samwright.framework.model.datatypes;

import java.util.List;

/**
 * Features set
 */
public interface Features {
    /**
     * Gets the enclosed features.
     *
     * @return the enclosed features.
     */
    List<Double> getFeatures();

    /**
     * The tag from the training data, or null if not training data.
     * @return tag from the training data, or null if not training data.
     */
    String getTag();
}

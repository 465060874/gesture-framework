package io.github.samwright.framework.model.common;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 13:02
 */
public interface Validatable {
    /**
     * Returns true iff this object, in itself, is valid.
     *
     * @return true iff this is valid.
     */
    boolean isValid();
}

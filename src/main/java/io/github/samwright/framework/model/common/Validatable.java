package io.github.samwright.framework.model.common;

/**
 * Implementors can be valid or invalid.
 */
public interface Validatable {
    /**
     * Returns true iff this object, in itself, is valid.
     *
     * @return true iff this is valid.
     */
    boolean isValid();
}

package io.github.samwright.framework.model.common;

/**
 * User: Sam Wright Date: 15/07/2013 Time: 15:13
 */
public interface MutationObserver {

    /**
     * Notify this that an observed {@link EventuallyImmutable} object has a new latest version.
     *
     * @param replacement
     */
    void notify(EventuallyImmutable replacement);


}

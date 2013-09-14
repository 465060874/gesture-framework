package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.mock.TopProcessor;

/**
 * The top controller, which is the only type of controller that a {@link TopProcessor} can have.
 */
public interface TopController extends ModelController {

    /**
     * If the controlled {@link TopProcessor} catches an exception,
     * it will pass it here to be properly handled (eg. by showing the exception in a modal popup
     * window).
     *
     * @param e the exception thrown whilst processing.
     */
    void handleExceptions(Exception e);
}

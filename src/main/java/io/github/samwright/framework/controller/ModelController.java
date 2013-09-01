package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Processor;

/**
 * User: Sam Wright Date: 31/08/2013 Time: 16:49
 */
public interface ModelController {
    void proposeModel(Processor proposedModel);

    ModelController createClone();

    void handleUpdatedModel();

    Processor getModel();
}

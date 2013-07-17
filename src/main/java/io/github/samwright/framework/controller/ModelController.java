package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.common.Replaceable;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public interface ModelController {

    void notify(Replaceable model);

    Replaceable getModel();
}

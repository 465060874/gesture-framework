package io.github.samwright.framework.model.common;

import io.github.samwright.framework.controller.ModelController;

/**
 * User: Sam Wright Date: 31/08/2013 Time: 13:54
 */
public interface Controllable {

    /**
     * Set the controller for this object, which is notified of replacements to this object.
     * <p/>
     * If future versions of this object already exist, they will all now use {@code
     * modelController} as their controller, and the controller will use this as its current model.
     *
     * @param modelController the controller for this object.
     */
    void setController(ModelController modelController);

    /**
     * Gets the controller for this object.
     *
     * @return the controller for this object.
     */
    ModelController getController();
}

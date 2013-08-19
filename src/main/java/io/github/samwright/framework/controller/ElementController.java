package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 22:53
 */
abstract public class ElementController extends ModelController<Element> {

    public ElementController(String fxmlResource) {
        super(fxmlResource);
    }

    public ElementController(ElementController toClone) {
        super(toClone);
    }
}

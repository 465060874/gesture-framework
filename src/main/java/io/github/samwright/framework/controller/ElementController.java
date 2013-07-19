package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 22:53
 */
abstract public class ElementController<I, O> extends ModelController<Element<I, O>> {

    public ElementController(String fxmlResource) {
        super(fxmlResource);
    }
}

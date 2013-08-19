package io.github.samwright.framework.controller.examples;

import io.github.samwright.framework.controller.LabelledElementController;

/**
 * User: Sam Wright Date: 13/08/2013 Time: 14:35
 */
public class Element1 extends LabelledElementController<Integer,String> {

    public Element1(String fxmlString, String labelString) {
        super(fxmlString, labelString);
    }

}

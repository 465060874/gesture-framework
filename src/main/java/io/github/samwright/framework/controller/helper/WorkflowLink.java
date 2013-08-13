package io.github.samwright.framework.controller.helper;

import javafx.scene.layout.Pane;

/**
 * User: Sam Wright Date: 25/07/2013 Time: 11:02
 */
public class WorkflowLink extends Pane {
    public WorkflowLink(String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);
    }


}

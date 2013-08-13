package io.github.samwright.framework.controller;

import javafx.scene.layout.FlowPane;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 17:42
 */
public class ToolboxController extends FlowPane {

    private void addTool(ModelController controller) {
        if (controller.getModel() == null)
            throw new RuntimeException("ModelController's model is null");

        
    }

}

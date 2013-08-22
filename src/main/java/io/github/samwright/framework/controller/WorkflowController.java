package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import javafx.scene.input.DataFormat;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 21:15
 */
abstract public class WorkflowController extends ModelController<Workflow> {

    public final static DataFormat dataFormat = new DataFormat("Workflow");

    public WorkflowController(String fxmlResource) {
        super(fxmlResource);
    }

    public WorkflowController(WorkflowController toClone) {
        super(toClone);
    }

    @Override
    public void handleUpdatedModel() {
        for (Element element : getModel().getChildren())
            element.getController().handleUpdatedModel();
    }

}

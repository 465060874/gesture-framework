package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import javafx.scene.input.DataFormat;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 21:15
 */
abstract public class WorkflowController extends JavaFXController {

    public final static DataFormat dataFormat = new DataFormat("Workflow");

    public WorkflowController(String fxmlResource) {
        super(fxmlResource);
    }

    public WorkflowController(WorkflowController toClone) {
        super(toClone);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        for (Element element : getModel().getCurrentVersion().getChildren())
            element.getController().handleUpdatedModel();
    }

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);

        getDefaultElementLink().setBeingDragged(beingDragged);

        for (Element element : getModel().getChildren())
            ((ElementController) element.getController()).setBeingDragged(beingDragged);
    }

    abstract public void setDefaultElementLink(ElementLink defaultElementLink);

    abstract public ElementLink getDefaultElementLink();

    @Override
    public Workflow getModel() {
        return (Workflow) super.getModel();
    }
}

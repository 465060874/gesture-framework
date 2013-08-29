package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
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

//        for (Element element : toClone.getModel().getChildren())
//            element.setController(element.getController().createClone());
//        if (getModel() == null)
//            throw new RuntimeException("why??");
    }

    @Override
    public void handleUpdatedModel() {
        for (Element element : getModel().getChildren())
            element.getController().handleUpdatedModel();
    }

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);

        getDefaultElementLink().setBeingDragged(beingDragged);

        for (Element element : getModel().getChildren())
            element.getController().setBeingDragged(beingDragged);
    }

    abstract public void setDefaultElementLink(ElementLink defaultElementLink);

    abstract public ElementLink getDefaultElementLink();
}

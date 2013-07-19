package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 21:15
 */
abstract public class WorkflowController<I, O> extends ModelController<Workflow<I, O>> {

    public WorkflowController(String fxmlResource) {
        super(fxmlResource);
    }

    @Override
    public void handleUpdatedModel() {
        for (Element<?, ?> element : getModel().getChildren())
            element.getController().handleUpdatedModel();
    }

}

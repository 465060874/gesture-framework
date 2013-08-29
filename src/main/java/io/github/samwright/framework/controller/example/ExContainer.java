package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractWorkflowContainer;
import io.github.samwright.framework.model.WorkflowContainer;
import io.github.samwright.framework.model.helper.Mediator;

/**
 * User: Sam Wright Date: 24/08/2013 Time: 09:17
 */
public class ExContainer extends AbstractWorkflowContainer {

    public ExContainer() {
        super();
    }

    public ExContainer(ExContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Mediator process(Mediator input) {
        return null; // Dummy implementation
    }

    @Override
    public WorkflowContainer createMutableClone() {
        return new ExContainer(this);
    }

    @Override
    public String getModelIdentifier() {
        return "ExContainer";
    }
}

package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.Mediator;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 09:21
 */
public class TopWorkflowContainer extends AbstractWorkflowContainer {

    public TopWorkflowContainer() {
        super();
    }

    public TopWorkflowContainer(TopWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Mediator process(Mediator input) {
        return null; // Dummy implementation
    }

    @Override
    public WorkflowContainer createMutableClone() {
        return new TopWorkflowContainer(this);
    }
}

package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.Mediator;
import lombok.Getter;
import lombok.Setter;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 09:21
 */
public class TopWorkflowContainer extends AbstractWorkflowContainer {

    @Getter @Setter private boolean transientUpdate = false;

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

    public TopWorkflowContainer getPreviousCompleted() {
        TopWorkflowContainer pointer = (TopWorkflowContainer) getPrevious();

        while (pointer != null && pointer.isTransientUpdate())
            pointer = (TopWorkflowContainer) pointer.getPrevious();

        return pointer;
    }

    public TopWorkflowContainer getNextCompleted() {
        TopWorkflowContainer pointer = (TopWorkflowContainer) getNext();

        while (pointer != null && pointer.isTransientUpdate())
            pointer = (TopWorkflowContainer) pointer.getNext();

        return pointer;
    }
}

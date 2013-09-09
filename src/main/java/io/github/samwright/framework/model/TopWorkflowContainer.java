package io.github.samwright.framework.model;

import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 09:21
 */
public class TopWorkflowContainer extends AbstractWorkflowContainer {

    @Getter @Setter private boolean transientUpdate = false;
    private Object[] processLock = new Object[0];

    public TopWorkflowContainer() {
        super(new TypeData(StartType.class, Object.class));
    }

    public TopWorkflowContainer(TopWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Mediator process(final Mediator input) {
        final List<Workflow> workflows = getChildren();

        synchronized (processLock) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Workflow workflow : workflows)
                        workflow.process(input);
                }
            }).start();
        }

        return null;
    }

    @Override
    public TopWorkflowContainer createMutableClone() {
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

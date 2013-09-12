package io.github.samwright.framework.model;

import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.mock.TopProcessor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 09:21
 */
public class TopWorkflowContainer extends AbstractWorkflowContainer implements TopProcessor {

    @Getter @Setter private boolean transientModel = false;
    private Object[] processLock = new Object[0];
    @Getter private boolean busy = false;

    public TopWorkflowContainer() {
        super(new TypeData(StartType.class, Object.class));
    }

    public TopWorkflowContainer(TopWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    public void process() {
        process(Mediator.createEmpty());
    }

    public void train() {
        processTrainingData(Mediator.createEmpty());
    }

    @Override
    public Mediator process(final Mediator input) {
        final List<Workflow> workflows = getChildren();

        if (busy)
            throw new RuntimeException("Cannot process new requests while busy");

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (processLock) {
                    busy = true;
                    try {
                        for (Workflow workflow : workflows) {
                            try {
                                workflow.process(input);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        busy = false;
                    }
                }
            }
        }).start();

        return null;
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        throw new RuntimeException("You have no good reason to call this.");
    }

    @Override
    public List<Mediator> processTrainingData(final Mediator input) {
        final List<Workflow> workflows = getChildren();

        if (busy)
            throw new RuntimeException("Cannot process new requests while busy");

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (processLock) {
                    busy = true;
                    try {
                        for (Workflow workflow : workflows) {
                            List<Mediator> completedTrainingData;
                            try {
                                completedTrainingData = workflow.processTrainingData(input);
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                continue;
                            }

                            CompletedTrainingBatch completedTrainingBatch = new CompletedTrainingBatch(
                                    new HashSet<>(completedTrainingData),
                                    Collections.<Mediator>emptySet()
                            );

                            workflow.processCompletedTrainingBatch(completedTrainingBatch);
                        }
                    } finally {
                        busy = false;
                    }
                }
            }
        }).start();

        return null;
    }

    @Override
    public TopWorkflowContainer createMutableClone() {
        return new TopWorkflowContainer(this);
    }

    public TopWorkflowContainer getPreviousCompleted() {
        TopWorkflowContainer pointer = (TopWorkflowContainer) getPrevious();

        while (pointer != null && pointer.isTransientModel())
            pointer = (TopWorkflowContainer) pointer.getPrevious();

        return pointer;
    }

    public TopWorkflowContainer getNextCompleted() {
        TopWorkflowContainer pointer = (TopWorkflowContainer) getNext();

        while (pointer != null && pointer.isTransientModel())
            pointer = (TopWorkflowContainer) pointer.getNext();

        return pointer;
    }
}

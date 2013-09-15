package io.github.samwright.framework.model;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.controller.TopController;
import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.mock.TopProcessor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;

/**
 * An implementation of {@link TopProcessor}.
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
        if (isValid() && areChildrenValid())
            process(Mediator.createEmpty());
    }

    public void train() {
        processTrainingData(Mediator.createEmpty());
    }

    @Override
    public Mediator process(final Mediator input) {
        final List<Workflow> workflows = getChildren();

        if (busy)
            return null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (processLock) {
                    try {
                        busy = true;
                        for (Workflow workflow : workflows) {
                            try {
                                workflow.process(input);
                            } catch (final RuntimeException e) {
                                busy = false;
                                getController().handleException(e);
                            }
                        }
                    } finally {
                        busy = false;
                        getController().handleException(null);
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
            return null;

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
                                getController().handleException(e);
                                continue;
                            }

                            HashSet<Mediator> outputMediators = new HashSet<>(completedTrainingData);
                            CompletedTrainingBatch completedTrainingBatch = new CompletedTrainingBatch(
                                    outputMediators,
                                    outputMediators
                            );

                            workflow.processCompletedTrainingBatch(completedTrainingBatch);
                        }
                    } finally {
                        busy = false;
                        getController().handleException(null);
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

    @Override
    public TopController getController() {
        return (TopController) super.getController();
    }

    @Override
    public void setController(ModelController controller) {
        if (controller instanceof TopController)
            super.setController(controller);
        else
            throw new RuntimeException("TopProcessor can only be controlled by a TopController, " +
                    "not a: " + controller.getClass());
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

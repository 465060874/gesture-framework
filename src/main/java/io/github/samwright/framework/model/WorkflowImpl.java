package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl extends AbstractWorkflow {

    /**
     * Constructs the initial (and immutable) {@code WorkflowImpl}.
     */
    public WorkflowImpl() {
        super();
    }

    /**
     * Constructs a mutable clone of the given {@code WorkflowImpl} with the given
     * {@link TypeData}.
     *
     * @param oldWorkflow the {@code WorkflowImpl} to clone.
     */
    public WorkflowImpl(WorkflowImpl oldWorkflow) {
        super(oldWorkflow);
    }


    @Override
    public boolean isValid() {
        if (getChildren().size() == 0) {
            return getTypeData().canBeEmptyContainer();
        } else {

            if ( !getChildren().get(1).getTypeData().canBeAtStartOfWorkflow(getTypeData()) ||
                    !getChildren().get(getChildren().size()-1).getTypeData().canBeAtEndOfWorkflow
                            (getTypeData()
                            ) )
                return false;

            TypeData previousType = null;

            for (Element e : getChildren()) {
                if (previousType != null)
                    if ( !e.getTypeData().canComeAfter(previousType) )
                        return false;

                previousType = e.getTypeData();
            }

            return true;
        }
    }

    @Override
    public Mediator process(Mediator input) {
        for (Element e : getChildren())
            input = e.process(input);

        return input;
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        for (Element e : getChildren())
            inputs = e.processTrainingBatch(inputs);

        return inputs;
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        ListIterator<Element> itr = getChildren().listIterator(getChildren().size());

        while (itr.hasPrevious()) {
            Processor child = itr.previous();
            completedTrainingBatch = child.processCompletedTrainingBatch(completedTrainingBatch);
        }

        return completedTrainingBatch;
    }

    @Override
    public Workflow createMutableClone() {
        return new WorkflowImpl(this);
    }
}

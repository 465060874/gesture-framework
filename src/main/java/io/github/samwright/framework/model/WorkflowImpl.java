package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl<I, O> extends AbstractWorkflow<I, O> {

    /**
     * Constructs the initial (and immutable) {@code WorkflowImpl} with the given
     * {@link TypeData}.
     *
     * @param typeData the input/output types of this object.
     */
    public WorkflowImpl(TypeData<I, O> typeData) {
        super(typeData);
    }

    /**
     * Constructs a mutable clone of the given {@code WorkflowImpl} with the given
     * {@link TypeData}.
     *
     * @param oldWorkflow the {@code WorkflowImpl} to clone.
     * @param typeData the input/output types of this object.
     */
    public WorkflowImpl(WorkflowImpl<I, O> oldWorkflow,
                        TypeData<I, O> typeData) {
        super(oldWorkflow, typeData);
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

            TypeData<?, ?> previousType = null;

            for (Element<?,?> e : getChildren()) {
                if (previousType != null)
                    if ( !e.getTypeData().canComeAfter(previousType) )
                        return false;

                previousType = e.getTypeData();
            }

            return true;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mediator<O> process(Mediator<?> input) {
        for (Element<?,?> e : getChildren())
            input = e.process(input);

        return (Mediator<O>) input;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<?>> inputs) {
        for (Element<?, ?> e : getChildren())
            inputs = (List<Mediator<?>>) (List<?>) e.processTrainingBatch(inputs);

        return (List<Mediator<O>>) (List<?>) inputs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletedTrainingBatch<I> processCompletedTrainingBatch(CompletedTrainingBatch<?> completedTrainingBatch) {
        ListIterator<Element<?, ?>> itr = getChildren().listIterator(getChildren().size());

        while (itr.hasPrevious()) {
            Processor child = itr.previous();
            completedTrainingBatch = child.processCompletedTrainingBatch(completedTrainingBatch);
        }

        return (CompletedTrainingBatch<I>) completedTrainingBatch;
    }

    @Override
    public WorkflowImpl<I, O> createMutableClone() {
        return new WorkflowImpl<>(this, getTypeData());
    }
}

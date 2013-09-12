package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.*;

/**
 * Implementation of Workflow.
 */
public class WorkflowImpl extends AbstractWorkflow {

    @Getter private List<Element> invalidlyOrderedElements;

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
        if (invalidlyOrderedElements == null) {
            invalidlyOrderedElements = new LinkedList<>();

            if (getChildren().size() == 0) {
                if (!getTypeData().canBeEmptyContainer())
                    invalidlyOrderedElements.add(null);

            } else {

                Element firstChild = getChildren().get(0);
                if (!firstChild.getTypeData().canBeAtStartOfWorkflow(getTypeData()))
                    invalidlyOrderedElements.add(null);

                Element finalChild = getChildren().get(getChildren().size() - 1);
                if (!finalChild.getTypeData().canBeAtEndOfWorkflow(getTypeData()))
                    invalidlyOrderedElements.add(finalChild);

                Element previousElement = null;

                for (Element element : getChildren()) {
                    if (previousElement != null)
                        if (!element.getTypeData().canComeAfter(previousElement.getTypeData()))
                            invalidlyOrderedElements.add(previousElement);

                    previousElement = element;
                }
            }
        }

        return invalidlyOrderedElements.isEmpty();
    }

    @Override
    public Mediator process(Mediator input) {
        for (Element e : getChildren()) {
            input = e.process(input);
            for (ElementObserver observer : e.getObservers())
                observer.notify(input);
        }

        return input.createNext(this, input.getData());
    }

    @Override
    public List<Mediator> processTrainingData(Mediator firstInput) {
        List<Mediator> outputs, inputs = Arrays.asList(firstInput);

        for (Element element : getChildren()) {
            outputs = new ArrayList<>();
            for (Mediator input : inputs)
                outputs.addAll(element.processTrainingData(input));
            inputs = outputs;
        }

        outputs = new ArrayList<>();
        for (Mediator input : inputs)
            outputs.add(input.createNext(this, input.getData()));

        return outputs;
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        completedTrainingBatch = super.processCompletedTrainingBatch(completedTrainingBatch);
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

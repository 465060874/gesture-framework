package BestSoFar.framework.core;

import BestSoFar.framework.helper.*;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Abstract implementor of Processor for elemental Processors to extend, which requires
 * a one-to-one mapping of input data to output data (without training necessary).
 */
public abstract class AbstractElement<I, O> implements Element<I,O> {

    @Delegate
    private final BestSoFar.framework.helper.Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    @Getter @Setter @NotNull private Workflow<?, ?> parent;

    public AbstractElement(Workflow<?, ?> parent) {
        setParent(parent);
    }

    public AbstractElement(AbstractElement<I, O> oldAbstractElement) {
        setParent(oldAbstractElement.getParent());
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<I>> inputs) {
        List<Mediator<O>> outputs = new LinkedList<>();

        for (Mediator<I> input : inputs)
            outputs.add(process(input));

        for (MediatorObserver<O> observer : getObservers())
            observer.notify(outputs);

        return outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> mapCompletedTrainingBatchBackward(List<Mediator<O>> completedOutputs,
                                                                           Set<Mediator<O>> successfulOutputs) {

        Map<Mediator<O>, Mediator<I>> mapping = new HashMap<>();

        for (Mediator<O> completedOutput : completedOutputs)
            mapping.put(completedOutput, (Mediator<I>) completedOutput.getPrevious());

        return mapping;
    }
}
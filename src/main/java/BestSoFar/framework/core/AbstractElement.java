package BestSoFar.framework.core;

import BestSoFar.framework.helper.*;
import BestSoFar.immutables.TypeData;
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

    @Delegate private final ProcessorObserverManager<O> observerManager = new ProcessorObserverManager<>();
    @Getter @Setter @NotNull private Workflow<?, ?> parent;
    @Getter @NotNull private final TypeData<I, O> typeData;

    public AbstractElement(Workflow<?, ?> parent, TypeData<I, O> typeData) {
        setParent(parent);
        this.typeData = typeData;
    }

    public AbstractElement(AbstractElement<?, ?> oldAbstractElement, TypeData<I, O> typeData) {
        setParent(oldAbstractElement.getParent());
        this.typeData = typeData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        Map<Mediator<O>, Mediator<I>> mapping = new HashMap<>();

        for (Mediator<?> completedOutput : completedOutputs)
            mapping.put((Mediator<O>) completedOutput, (Mediator<I>) completedOutput.getPrevious());

        return mapping;
    }
}
package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.ObservableProcess;
import BestSoFar.framework.core.helper.*;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.core.helper.ObserverSet;
import BestSoFar.framework.core.helper.ParentBox;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract implementor of {@link Processor} for elemental Processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete Element implementations can derive from this to let it handle the boilerplate code
 * (accessors for parent and {@link TypeData}, and {@link BestSoFar.framework.core.common.ProcessObserver} management).
 */
public abstract class AbstractElement<I, O> implements Element<I, O> {

    @Delegate private final ObservableProcess<O> observers;
    @Getter @NonNull private final TypeData<I, O> typeData;
    @Delegate private final ChildOf<Workflow<?, ?>> parentManager;
    @Delegate private final ProcessorMutationHandler<I, O, ?, ?> mutationHandler =
            new ProcessorMutationHandler<>(this);


    @SuppressWarnings("unchecked")
    public AbstractElement(Workflow<?, ?> parent, TypeData<I, O> typeData) {
        this.typeData = typeData;
        observers = new ObserverSet<>(this);
        parentManager = new ParentBox<Workflow<?, ?>>(parent, this);
    }

    @SuppressWarnings("unchecked")
    public AbstractElement(AbstractElement<?, ?> oldAbstractElement, TypeData<I, O> typeData) {
        this.typeData = typeData;
        observers = ((ObserverSet<O>) oldAbstractElement.observers).assignReplacementTo(this);
        parentManager = ((ParentBox<Workflow<?, ?>>) oldAbstractElement.parentManager)
                                .assignReplacementTo(this);
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

    @Override
    public <I2, O2> void replaceSelfWithClone(Processor<I2, O2> clone) {
        AbstractElement<I, O> replacement = (AbstractElement<I, O>) clone;

        // If this returns false, then I have been previously disowned and nothing happens.
        getParent().getElements().replace(this, replacement);
    }
}
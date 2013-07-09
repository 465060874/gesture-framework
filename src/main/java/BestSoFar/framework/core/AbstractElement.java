package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.core.helper.*;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.ImmutableSet;
import BestSoFar.framework.immutables.SelfReplacingImmutableImpl;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
import lombok.Delegate;
import lombok.Getter;

import java.util.*;

/**
 * Abstract implementation of {@link Processor} for elemental Processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete Element implementations can derive from this to let it handle the boilerplate code
 * (accessors for parent and {@link TypeData}, and {@link BestSoFar.framework.core.common.ProcessObserver} management).
 */
public abstract class AbstractElement<I, O> implements Element<I, O> {

    @Getter private ImmutableSet<ProcessObserver<O>> observers;
    @Getter private final TypeData<I, O> typeData;
    @Getter private Workflow<?, ?> parent;

    @Delegate(excludes = SelfReplacingImmutableImpl.ToOverride.class)
    private final SelfReplacingImmutableImpl replacementManager;


    public AbstractElement(TypeData<I, O> typeData, boolean mutable) {
        this.typeData = typeData;
        observers = new ImmutableSet<>(false);
        observers.assignToHandler(this);
        replacementManager = new SelfReplacingImmutableImpl(mutable);
    }

    @SuppressWarnings("unchecked")
    public AbstractElement(AbstractElement<?, ?> oldAbstractElement,
                           TypeData<I, O> typeData, boolean mutable) {
        this.typeData = typeData;
        this.observers = (ImmutableSet<ProcessObserver<O>>) (ImmutableSet<?>)
                oldAbstractElement.observers.createClone(false);
        replacementManager = new SelfReplacingImmutableImpl(mutable);
    }

    @Override
    public void setParent(Workflow<?, ?> parent) {
        if (this.parent == parent)
            return;

        if (isMutable())
            this.parent = parent;
        else {
            AbstractElement<I, O> replacement = createClone(true);
            replacement.setParent((Workflow<?, ?>) parent.getLatest());
            proposeReplacement(replacement);
        }
    }

    @Override
    abstract public AbstractElement<I, O> createClone(boolean mutable);

    @Override
    public void delete() {
        replacementManager.delete();

        if (!parent.isDeleted())
            getParent().getElements().remove(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractElement<I, O> getReplacement() {
        return (AbstractElement<I, O>) replacementManager.getReplacement();
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractElement<I, O> getReplaced() {
        return (AbstractElement<I, O>) replacementManager.getReplaced();
    }

    @Override
    public void finalise() {

        if (!parent.isMutable())
            parent.getElements().replaceOrAdd(getReplaced(), this);

        observers = Observers.updateObservers(observers);
        observers.assignToHandler(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReplacement(EventuallyImmutable existingObject, EventuallyImmutable proposedObject) {
        if (observers == existingObject) {
            if (isMutable()) {
                observers = (ImmutableSet<ProcessObserver<O>>) proposedObject;
            } else {
                AbstractElement<I, O> replacement = createClone(true);
                getReplacement().observers = (ImmutableSet<ProcessObserver<O>>) proposedObject;
                getReplacement().observers.assignToHandler(replacement);
                proposeReplacement(replacement);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        List<Mediator<O>> castedOutputs = (List<Mediator<O>>) (List<?>) completedOutputs;
        return  Mediator.create1to1BackwardMapping(castedOutputs);
    }


}
package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ElementObserver;
import BestSoFar.framework.core.helper.*;
import lombok.Delegate;
import lombok.Getter;

import java.util.*;

/**
 * Abstract implementation of {@link Element} for elemental processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete {@code Element} implementations can derive from this to let it handle its parent
 * {@link Workflow}, its {@link TypeData}, mutation management, and
 * {@link BestSoFar.framework.core.common.ElementObserver} management).
 */
public abstract class AbstractElement<I, O> implements Element<I, O> {
    @Getter private Set<ElementObserver<O>> observers;
    @Getter private final TypeData<I, O> typeData;
    private final ParentManager<Element<?, ?>, Workflow<?, ?>> parentManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper mutabilityHelper;

    /**
     * Constructs the initial (and immutable) {@code AbstractElement} with the given
     * {@link TypeData}.
     *
     * @param typeData the input/output types of this object.
     */
    public AbstractElement(TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, false);
        observers = Collections.emptySet();
        parentManager = new ParentManager<Element<?, ?>, Workflow<?, ?>>(this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractElement} with the given
     * {@link TypeData}.
     *
     * @param oldElement the {@code AbstractElement} to clone.
     * @param typeData the input/output types of this object.
     */
    @SuppressWarnings("unchecked")
    public AbstractElement(AbstractElement<?, ?> oldElement, TypeData<I, O> typeData) {
        if (oldElement.isMutable())
            throw new RuntimeException("Cannot clone an immutable object");
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, true);
        this.observers = (Set<ElementObserver<O>>) (Set<?>) oldElement.getObservers();
        parentManager = new ParentManager<Element<?, ?>, Workflow<?, ?>>(this, oldElement.getParent());
        // TODO: use Set<ElementObserver<?>> observers. validation should include ? -> O check.
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element<I, O> withParent(Workflow<?, ?> newParent) {
        return (Element<I, O>) parentManager.withParent(newParent);
    }

    @Override
    public Workflow<?, ?> getParent() {
        return parentManager.getParent();
    }

    @Override
    public void discardNext() {
        mutabilityHelper.discardNext();
    }

    @Override
    public void discardPrevious() {
        mutabilityHelper.discardPrevious();
    }

    @Override
    abstract public AbstractElement<I, O> createMutableClone();

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.afterDelete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        parentManager.beforeFixAsVersion(versionInfo);

        if (isMutable()) {
            // Make mutable clone of observers set
            observers = new HashSet<>(observers);
            // Update observers to their latest versions
            VersionInfo.updateAllToLatest(observers);
        }

        mutabilityHelper.fixAsVersion(versionInfo);
    }

    @Override
    public Element<I, O> withObservers(Set<ElementObserver<O>> newObservers) {
        if (isMutable()) {
            observers = Collections.unmodifiableSet(newObservers);
            return this;
        } else {
            return createMutableClone().withObservers(newObservers);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletedTrainingBatch<I> processCompletedTrainingBatch(CompletedTrainingBatch<?> completedTrainingBatch) {
        Set<Mediator<O>> allOutputs = (Set<Mediator<O>>) (Set<?>) completedTrainingBatch.getAll();

        Set<Mediator<O>> successfulOutputs = (Set<Mediator<O>>) (Set<?>)
                completedTrainingBatch.getSuccessful();

        Set<Mediator<I>> allInputs = Mediator.rollbackMediators(allOutputs);
        Set<Mediator<I>> successfulInputs = Mediator.rollbackMediators(successfulOutputs);

        return new CompletedTrainingBatch<>(allInputs, successfulInputs);
    }

}
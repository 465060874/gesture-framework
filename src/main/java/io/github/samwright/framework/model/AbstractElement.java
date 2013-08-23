package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.*;
import lombok.Delegate;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract implementation of {@link Element} for elemental processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete {@code Element} implementations can derive from this to let it handle its parent
 * {@link Workflow}, its {@link TypeData}, mutation management, and
 * {@link io.github.samwright.framework.model.common.ElementObserver} management).
 */
public abstract class AbstractElement implements Element {
    @Getter private Set<ElementObserver> observers;
    private final ParentManager<Element, Workflow> parentManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper<Element> mutabilityHelper;

    @Delegate
    private final TypeDataManager<Element> typeDataManager;

    /**
     * Constructs the initial (and immutable) {@code AbstractElement}.
     */
    public AbstractElement() {
        typeDataManager = new TypeDataManager<Element>(this);
        mutabilityHelper = new MutabilityHelper<Element>(this, false);
        observers = Collections.emptySet();
        parentManager = new ParentManager<Element, Workflow>(this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractElement}.
     *
     * @param oldElement the {@code AbstractElement} to clone.
     */
    public AbstractElement(AbstractElement oldElement) {
        if (oldElement.isMutable())
            throw new RuntimeException("Cannot clone a mutable object");

        typeDataManager = new TypeDataManager<Element>(this, oldElement.getTypeData());
        mutabilityHelper = new MutabilityHelper<Element>(this, true);
        this.observers = oldElement.getObservers();
        parentManager = new ParentManager<Element, Workflow>(this, oldElement.getParent());
        // TODO: use Set<ElementObserver<?>> observers. validation should include ? -> O check.
    }

    @Override
    public Element withParent(Workflow newParent) {
        return parentManager.withParent(newParent);
    }

    @Override
    public Workflow getParent() {
        return parentManager.getParent();
    }

    @Override
    public void discardNext() {
        mutabilityHelper.discardNext();
        parentManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        mutabilityHelper.discardPrevious();
        parentManager.discardPrevious();
    }

    @Override
    public void redo() {
        mutabilityHelper.redo();
    }

    @Override
    public void undo() {
        mutabilityHelper.undo();
    }

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.afterDelete();
    }

    @Override
    public Element createOrphanedDeepClone() {
        if (isMutable())
            throw new RuntimeException("Cannot clone mutable");

        Element clone = createMutableClone().withParent(null);
        this.replaceWith(clone);
        this.discardNext();
        return clone;
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
    public Element withObservers(Set<ElementObserver> newObservers) {
        if (isMutable()) {
            observers = Collections.unmodifiableSet(newObservers);
            return this;
        } else {
            return createMutableClone().withObservers(newObservers);
        }
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        Set<Mediator> allOutputs = completedTrainingBatch.getAll();

        Set<Mediator> successfulOutputs = completedTrainingBatch.getSuccessful();

        Set<Mediator> allInputs = Mediator.rollbackMediators(allOutputs);
        Set<Mediator> successfulInputs = Mediator.rollbackMediators(successfulOutputs);

        return new CompletedTrainingBatch(allInputs, successfulInputs);
    }


    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }
}
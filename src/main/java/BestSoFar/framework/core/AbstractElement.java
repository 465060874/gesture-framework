package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.core.helper.*;
import lombok.Delegate;
import lombok.Getter;

import java.util.*;

/**
 * Abstract implementation of {@link Processor} for elemental Processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete Element implementations can derive from this to let it handle parent management,
 * the accessor for {@link TypeData}, and {@link ProcessObserver} management).
 */
public abstract class AbstractElement<I, O> implements Element<I, O> {
    @Getter private Set<ProcessObserver<O>> observers;
    @Getter private final TypeData<I, O> typeData;
    private final ParentManager<Element<?, ?>, Workflow<?, ?>> parentManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper mutabilityHelper;

    {
        parentManager = new ParentManager<Element<?, ?>, Workflow<?, ?>>(this);
    }

    public AbstractElement(TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, false);
        observers = Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public AbstractElement(AbstractElement<?, ?> oldAbstractElement, TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutabilityHelper = new MutabilityHelper(this, true);
        this.observers = (Set<ProcessObserver<O>>) (Set<?>) oldAbstractElement.getObservers();
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
    public void discardReplacement() {
        mutabilityHelper.discardReplacement();
    }

    @Override
    public void discardOlderVersions() {
        mutabilityHelper.discardOlderVersions();
    }

    @Override
    abstract public AbstractElement<I, O> createMutableClone();

    @Override
    public void delete() {
        mutabilityHelper.delete();
        parentManager.delete();
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        if (isMutable()) {
            parentManager.finalise(versionInfo);

            // Make mutable clone of observers set
            observers = new HashSet<>(observers);
            // Update observers to their latest versions
            VersionInfo.updateAllToLatest(observers);
        }

        mutabilityHelper.fixAsVersion(versionInfo);
    }

    @Override
    public Element<I, O> withObservers(Set<ProcessObserver<O>> newObservers) {
        if (isMutable()) {
            observers = Collections.unmodifiableSet(newObservers);
            return this;
        } else {
            return createMutableClone().withObservers(newObservers);
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
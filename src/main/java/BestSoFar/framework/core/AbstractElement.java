package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ObservableProcess;
import BestSoFar.framework.core.common.ProcessObserver;
import BestSoFar.framework.core.helper.*;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
import BestSoFar.framework.immutables.ImmutableVersion;
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
    @Getter private Set<ProcessObserver<O>> observers;
    @Getter private final TypeData<I, O> typeData;
    @Getter private ImmutableVersion version = new ImmutableVersion(this);
    @Getter private boolean mutable, deleted;
    private final ParentManager<Element<?, ?>, Workflow<?, ?>> parentManager;

    {
        parentManager = new ParentManager<>((Element<?, ?>) this);
        deleted = false;
    }

    public AbstractElement(TypeData<I, O> typeData) {
        this.typeData = typeData;
        this.mutable = false;
        observers = Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public AbstractElement(AbstractElement<?, ?> oldAbstractElement, TypeData<I, O> typeData) {
        this.typeData = typeData;
        this.mutable = true;
        this.observers = (Set<ProcessObserver<O>>) oldAbstractElement.getObservers();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Element<I, O> withParent(Workflow<?, ?> parent) {
        return (Element<I, O>) parentManager.withParent(parent);
    }

    @Override
    abstract public AbstractElement<I, O> createMutableClone();

    @Override
    public void delete() {
        deleted = true;
        parentManager.delete();
    }

    @Override
    public void finalise(ImmutableVersion version) {
        if (!isMutable())
            throw new RuntimeException("Already finalised!");

        this.version = version;
        parentManager.finalise(version);

        // Make mutable clone of observers set
        observers = new HashSet<>(observers);
        // Update observers to their latest versions
        ImmutableVersion.updateAllToLatest(observers);

        mutable = false;
    }

    @Override
    public ObservableProcess<O> withObservers(Set<ProcessObserver<O>> newObservers) {
        if (isMutable()) {
            observers = Collections.unmodifiableSet(newObservers);
            return this;
        } else {
            return createMutableClone().withObservers(newObservers);
        }
    }

    @Override
    public void replaceWith(EventuallyImmutable proposed) {
        ImmutableVersion nextVersion = proposed.getVersion().withPrevious(this);
        version = getVersion().withNext(proposed);
        proposed.finalise(nextVersion);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<?>> completedOutputs,
                                                                               Set<Mediator<?>> successfulOutputs) {

        List<Mediator<O>> castedOutputs = (List<Mediator<O>>) (List<?>) completedOutputs;
        return  Mediator.create1to1BackwardMapping(castedOutputs);
    }


}
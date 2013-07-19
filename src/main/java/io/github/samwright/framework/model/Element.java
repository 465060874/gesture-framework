package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.Set;

/**
 * An {@code Element} is a {@link Processor} which sits inside a {@link Workflow}.
 *
 * It can be observed by {@link ElementObserver} objects, which are registered with the
 * {@code withObservers(newObservers)} method.
 */
public interface Element<I, O>
        extends Processor<I, O>, ChildOf<Workflow<?, ?>> {

    /**
     * Gets the {@link ElementObserver} objects that are observing this.  When this is
     * replaced, the observers are updated to their latest versions.
     *
     * @return the {@link ElementObserver} objects that are observing this.
     */
    Set<ElementObserver<O>> getObservers();

    /**
     * Returns this (or if this is not mutable, a clone) with the given set of observers.
     *
     * @param newObservers the observers who's latest versions will be in the returned
     *                     {@code Element}.
     * @return an {@code Element} with the latest versions of the given {@code newObservers}.
     */
    Element<I, O> withObservers(Set<ElementObserver<O>> newObservers);

    @Override
    Element<I, O> withParent(Workflow<?, ?> newParent);

    @Override
    <I2, O2> Element<I2, O2> withTypeData(TypeData<I2, O2> newTypeData);
}

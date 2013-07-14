package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.ElementObserver;

import java.util.Set;

/**
 * An {@code Element} is a {@link Processor} which sits inside a {@link Workflow}.
 *
 * It can be observed by {@link ElementObserver} objects, which are registered with the
 * {@code withObservers(newObservers)} method.
 */
public interface Element<I, O>
        extends Processor<I, O>, ChildOf<Workflow<?, ?>> {

    Element<I, O> withParent(Workflow<?, ?> newParent);

    Set<ElementObserver<O>> getObservers();

    Element<I, O> withObservers(Set<ElementObserver<O>> newObservers);
}
package BestSoFar.framework.core.common;

import BestSoFar.framework.core.helper.Mediator;

import java.util.List;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 15:53
 */
public interface ObservableProcess<T> {

    public Set<ProcessObserver<T>> getObservers();

    public ObservableProcess<T> withObservers(Set<ProcessObserver<T>> newObservers);
}

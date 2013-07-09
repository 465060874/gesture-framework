package BestSoFar.framework.core;

import BestSoFar.framework.core.common.ChildOf;

import java.util.List;

/**
 * User: Sam Wright Date: 09/07/2013 Time: 16:15
 */
public interface ParentOf<C extends ChildOf<?>> {
    List<C> getChildren();

    ParentOf<C> withChildren(List<C> newChildren);
}

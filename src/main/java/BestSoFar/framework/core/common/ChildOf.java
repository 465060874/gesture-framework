package BestSoFar.framework.core.common;

import BestSoFar.framework.core.ParentOf;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 18:43
 */
public interface ChildOf<P extends ParentOf<?>> {
    P getParent();

    ChildOf<P> withParent(P parent);
}

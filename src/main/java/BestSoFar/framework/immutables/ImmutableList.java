package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.ImmutableReplacement;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 14:32
 */
public interface ImmutableList<E>
        extends List<E>, ImmutableReplacement<ImmutableList<E>> {

    boolean replace(E oldElement, E newElement);
}

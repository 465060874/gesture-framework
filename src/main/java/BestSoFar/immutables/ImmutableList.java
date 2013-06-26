package BestSoFar.immutables;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 14:32
 */
public interface ImmutableList<E> extends List<E> {
    boolean replace(E oldElement, E newElement);

    List<E> getMutatedList();
}

package FailedLists;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 09:18
 */
public interface NewImmutableList<E> extends List<E> {
    List<E> getNextList();

    void replace(E oldElement, E newElement);
}

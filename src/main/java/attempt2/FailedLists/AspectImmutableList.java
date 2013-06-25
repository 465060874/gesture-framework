package attempt2.FailedLists;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 09:59
 */
public interface AspectImmutableList<E> extends List<E> {
    AspectImmutableList<E> getNextList();

    void replace(E oldElement, E newElement);
}

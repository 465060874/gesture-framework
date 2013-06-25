package attempt2.FailedLists;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 18:57
 */
public interface ImmutableList<T> {
    void add(int index, T element);

    void remove(int index);

    void replace(T oldElement, T newElement);

    T get(int index);

    boolean isEmpty();

    int indexOf(Object element);

    int size();

    List<T> getNextList();
}

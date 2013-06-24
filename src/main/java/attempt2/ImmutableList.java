package attempt2;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:19
 */
public interface ImmutableList<T> {
    ImmutableList<T> insert(int index, T element);

    ImmutableList<T> remove(int index);

    T get(int index);

    int indexOf(T element);

    int size();
}

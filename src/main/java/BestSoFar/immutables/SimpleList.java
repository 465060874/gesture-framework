package BestSoFar.immutables;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 12:12
 */
interface SimpleList<E> {

    List<E> getList();

     /*
        All of List<E> except:
     boolean addAll(Collection<? extends E> c);
     boolean addAll(int index, Collection<? extends E> c);
     <T> T[] toArray(T[] a);
      */

    int size();

    boolean isEmpty();

    boolean contains(Object o);

    Iterator<E> iterator();

    Object[] toArray();

//    <T> T[] toArray(T[] a);

//    boolean add(E t);

    boolean remove(Object o);

    boolean containsAll(Collection<?> c);

//    boolean addAll(Collection<? extends E> c);

    boolean addAll(int index, Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    void clear();

    E get(int index);

    E set(int index, E element);

    void add(int index, E element);

    E remove(int index);

    int indexOf(Object o);

    int lastIndexOf(Object o);

    ListIterator<E> listIterator();

    ListIterator<E> listIterator(int index);

    List<E> subList(int fromIndex, int toIndex);
}

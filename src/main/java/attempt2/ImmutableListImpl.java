package attempt2;

import lombok.Delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:19
 */
public class ImmutableListImpl<T> implements ImmutableList<T> {

    private interface ReadOnlyMethods<T> {
        T get(int index);
        int indexOf(Object o);
        int size();
        boolean isEmpty();
    }

    @Delegate(types = ReadOnlyMethods.class)
    private final List<T> list;
    private final ImmutableListHandler handler;

    private List<T> nextList;

    public ImmutableListImpl(List<T> list, ImmutableListHandler handler) {
        this.handler = handler;
        this.list = Collections.unmodifiableList(list);
        nextList = this.list;
    }

    private List<T> duplicateList() {
        return new ArrayList<>(list);
    }

    @Override
    public List<T> getNextList() {
        List<T> temp = nextList;
        nextList = list;
        return temp;
    }

    @Override
    public void add(int index, T element) {
        nextList = duplicateList();
        nextList.add(index, element);
        handleNewList();
    }

    @Override
    public void remove(int index) {
        nextList = duplicateList();
        nextList.remove(index);
        handleNewList();
    }

    @Override
    public void replace(T oldElement, T newElement) {
        nextList = duplicateList();
        int index = indexOf(oldElement);
        nextList.set(index, newElement);
        handleNewList();
    }

    private void handleNewList() {
        handler.handleNewList();
    };

}
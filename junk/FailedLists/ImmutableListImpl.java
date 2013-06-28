package FailedLists;

/**
 * User: Sam Wright
 * Date: 22/06/2013
 * Time: 16:19
 */
public class ImmutableListImpl<T> {} /* implements ImmutableList<T> {

    private interface ReadOnlyMethods<T> {
        T get(int index);
        int indexOf(Object o);
        int size();
        boolean isEmpty();
    }

    @Delegate(types = ReadOnlyMethods.class)
    private final List<T> list;
    private final ReplaceOnMutate handler;

    private List<T> nextList;

    public ImmutableListImpl(List<T> list, ReplaceOnMutate handler) {
        this.handler = handler;
        this.list = Collections.unmodifiableList(list);
        nextList = this.list;
    }

    public ImmutableListImpl(ReplaceOnMutate handler) {
        this(Collections.<T>emptyList(), handler);
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
        handler.handleMutation();
    }

}*/
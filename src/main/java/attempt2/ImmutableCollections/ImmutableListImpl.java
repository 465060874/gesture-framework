package attempt2.ImmutableCollections;

import attempt2.FailedLists.SimpleList;
import attempt2.FailedLists.SimpleListImpl;
import attempt2.ImmutableListHandler;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 13:47
 */

public class ImmutableListImpl<E> implements ImmutableList<E> {

    // Delegation and setup

    @Delegate private SimpleList<E> activeList;
    @Getter private List<E> mutatedList;
    private final SimpleList<E> backupList;

    private final ImmutableListHandler<E> handler;

    public ImmutableListImpl(@NotNull List<E> list, @NotNull ImmutableListHandler<E> handler) {
        this.handler = handler;
        this.activeList = new SimpleListImpl<>(Collections.unmodifiableList(list));
        this.backupList = activeList;
    }

    public ImmutableListImpl(@NotNull ImmutableListHandler<E> handler) {
        this(Collections.<E>emptyList(), handler);
    }


    // Mutation handling methods

    protected void makeMutable() {
        mutatedList = new ArrayList<>(backupList.getList());
        activeList = new SimpleListImpl<>(mutatedList);
    }

    protected void notifyMutationHandler() {
        handler.handleMutatedList();
        activeList = backupList;
        mutatedList = null;
    }


    // Implementation of problematic List methods

    @Override
    public boolean replace(E oldElement, E newElement) {
        int index = indexOf(oldElement);
        return index != -1 && set(index, newElement) == oldElement;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return activeList.getList().toArray(a);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false; // Dummy implementation
    }


}

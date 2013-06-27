package BestSoFar.immutables;

import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

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

    private final ReplaceOnMutate handler;

    public ImmutableListImpl(@NonNull List<E> list, @NonNull ReplaceOnMutate handler) {
        this.handler = handler;
        this.activeList = new SimpleListImpl<>(Collections.unmodifiableList(list));
        this.backupList = activeList;
    }

    public ImmutableListImpl(@NonNull ReplaceOnMutate handler) {
        this(Collections.<E>emptyList(), handler);
    }


    // Mutation handling methods

    protected void makeMutable() {
        System.out.println("I am mutable now!!");
        mutatedList = new ArrayList<>(backupList.getList());
        activeList = new SimpleListImpl<>(mutatedList);
    }

    protected void notifyMutationHandler() {
        handler.handleMutation();
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
    public boolean add(E e) {
        return activeList.getList().add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return activeList.getList().addAll(c);
    }


}

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

    // TODO: split some functionality into AbstractImmutableList<E>

    @Getter private ImmutableListImpl<E> replacement;
    @Delegate private SimpleList<E> activeList;
    private SimpleList<E> mutatedList;
    private final SimpleList<E> backupList;
    private final MutationHandler mutationHandler;
    private Lock lock = new Lock();

    // Delegation and setup

    public ImmutableListImpl(@NonNull ReplaceOnMutate handler) {
        this(Collections.<E>emptyList(), handler);
    }

    private ImmutableListImpl(@NonNull List<E> list, @NonNull MutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        this.activeList = new SimpleListImpl<>(Collections.unmodifiableList(list));
        mutatedList = backupList = activeList;
    }

    @Override
    public ImmutableList<E> makeReplacementFor(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new AlreadyMutatedException();

        return new ImmutableListImpl<>(mutatedList.getList(), mutationHandler);
    }

    @Override
    public void forgetReplacement() {
        mutatedList = backupList;
        replacement = null;
    }

    @Override
    public boolean hasReplacement() {
        return replacement != null;
    }

    @Override
    public boolean replacementIsMutated() {
        return mutatedList != backupList;
    }


    // Mutation handling methods

    /**
     * Called before any method is called.
     */
    protected void startRead() {
        lock.getReadLock();
    }

    /**
     * Called after any method returns (but before 'startMutation()' and 'endMutation',
     * if they are called).
     */
    protected void endRead() {
        lock.releaseReadLock();
    }

    /**
     * Called if method tried to mutate, so threw UnsupportedOperationException
     */
    protected void startMutation() {
        lock.getWriteLock();

        if (hasReplacement() || replacementIsMutated())
            throw new AlreadyMutatedException();

        mutatedList = new SimpleListImpl<>(new ArrayList<>(backupList.getList()));
        activeList = mutatedList;
    }

    /**
     * If method called was a mutation (so 'startMutation()' was called) this is called after
     * the method returns.
     */
    protected void endMutation() {
        activeList = backupList;
        mutationHandler.handleMutation();

        lock.releaseWriteLock();
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

package BestSoFar.immutables;

import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 13:47
 */

public class ImmutableListImpl<E> implements ImmutableList<E> {

    // TODO: split some functionality into AbstractImmutableList<E>

    @Getter private ImmutableListImpl<E> replacement;
    @Delegate private List<E> activeList;
    private List<E> mutatedList;
    private final List<E> backupList;
    private final MutationHandler mutationHandler;
    private Lock lock = new Lock();

    // Delegation and setup

    public ImmutableListImpl(ReplaceOnMutate handler) {
        this(Collections.<E>emptyList(), handler);
    }

    private ImmutableListImpl(List<E> list, @NonNull MutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        this.activeList = Collections.unmodifiableList(list);
        mutatedList = backupList = activeList;
    }

    @Override
    public ImmutableList<E> makeReplacementFor(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new AlreadyMutatedException();

        replacement = new ImmutableListImpl<>(mutatedList, mutationHandler);
        return replacement;
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
     * Called after any method returns or throws exception (but before 'startMutation()' and
     * 'endMutation', if they are called).
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

        if (ArrayList.class.isAssignableFrom(backupList.getClass()))
            mutatedList = new ArrayList<>(backupList);
        else
            mutatedList = new LinkedList<>(backupList);

        activeList = mutatedList;
    }

    /**
     * If method called was a mutation (so 'startMutation()' was called) this is called after
     * the method returns (or throws exception).
     */
    protected void endMutation() {
        activeList = backupList;
        mutationHandler.handleMutation();

        lock.releaseWriteLock();
    }


    // Extra List methods

    @Override
    public boolean replace(E oldElement, E newElement) {
        int index = indexOf(oldElement);
        return index != -1 && set(index, newElement) == oldElement;
    }

    @Override
    public int hashCode() {
        return backupList.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return backupList.equals(obj);
    }

    @Override
    public String toString() {
        return backupList.toString();
    }
}

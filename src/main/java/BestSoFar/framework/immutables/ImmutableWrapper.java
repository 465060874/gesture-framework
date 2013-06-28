package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.ImmutableReplacement;
import BestSoFar.framework.immutables.common.MutationHandler;
import BestSoFar.framework.immutables.helper.Lock;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 20:14
 */
public abstract class ImmutableWrapper<T>
        implements ImmutableReplacement<ImmutableWrapper<T>> {

    public static @ interface DoNotAdvise {}

    private ImmutableWrapper<T> replacement;
    private T mutated;
    private T immutable;
    private final MutationHandler mutationHandler;
    private Lock lock = new Lock();

    // Delegation and setup

    public ImmutableWrapper(T delegate, @NonNull MutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        mutated = immutable = delegate;
        setActiveDelegate(delegate);
    }

    @DoNotAdvise
    @Override
    public ImmutableWrapper<T> getReplacement() {
        return replacement;
    }

    @DoNotAdvise
    @Override
    public ImmutableWrapper<T> makeReplacementFor(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new ImmutableReplacement.AlreadyMutatedException();

        replacement = createNewFromMutated(mutated, mutationHandler);
        return replacement;
    }

    @DoNotAdvise
    abstract ImmutableWrapper<T> createNewFromMutated(T mutated, MutationHandler mutationHandler);

    @DoNotAdvise
    abstract T cloneDelegateAsMutable(T delegate);

    @DoNotAdvise
    abstract void setActiveDelegate(T delegate);

    @DoNotAdvise
    @Override
    public void forgetReplacement() {
        mutated = immutable;
        replacement = null;
    }

    @DoNotAdvise
    @Override
    public boolean hasReplacement() {
        return replacement != null;
    }

    @DoNotAdvise
    @Override
    public boolean replacementIsMutated() {
        return mutated != immutable;
    }


    // Mutation handling methods

    /**
     * Called before any method is called.
     */
    @DoNotAdvise
    protected void startRead() {
        lock.getReadLock();
    }

    /**
     * Called after any method returns or throws exception (but before 'startMutation()' and
     * 'endMutation', if they are called).
     */
    @DoNotAdvise
    protected void endRead() {
        lock.releaseReadLock();
    }

    /**
     * Called if method tried to mutate, so threw UnsupportedOperationException
     */
    @DoNotAdvise
    protected void startMutation() {
        lock.getWriteLock();

        if (hasReplacement() || replacementIsMutated())
            throw new ImmutableReplacement.AlreadyMutatedException();

        mutated = cloneDelegateAsMutable(immutable);

        setActiveDelegate(mutated);
    }

    /**
     * If method called was a mutation (so 'startMutation()' was called) this is called after the
     * method returns (or throws exception).
     */
    @DoNotAdvise
    protected void endMutation() {
        setActiveDelegate(immutable);
        mutationHandler.handleMutation();

        lock.releaseWriteLock();
    }

    @DoNotAdvise
    @Override
    public int hashCode() {
        return immutable.hashCode();
    }

    @DoNotAdvise
    @Override
    public boolean equals(Object obj) {
        return immutable.equals(obj);
    }

    @DoNotAdvise
    @Override
    public String toString() {
        return immutable.toString();
    }
}

package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.ImmutableReplacement;
import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Getter;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 00:17
 */
public abstract class ImmutableWrapper<T> implements ImmutableReplacement {

    public static @interface DoNotAdvise {}

    private ImmutableWrapper<T> replacement;
    private MutationHandler mutationHandler;
    @Getter private boolean isMutated;

    // Delegation and setup

    @DoNotAdvise
    @Override
    public ImmutableWrapper<T> getReplacement() {
        if (hasReplacement())
            return replacement;
        else
            return null;
    }

    /**
     * First-time constructor.  Subsequent objects are created using assignReplacementTo(..).
     * @param mutationHandler
     */
    public ImmutableWrapper(MutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        isMutated = false;
    }

    /**
     * Constructor used when subclass clones.
     */
    public ImmutableWrapper() {};

    @DoNotAdvise
    @Override
    public ImmutableWrapper<T> assignReplacementTo(MutationHandler mutationHandler) {
        if (hasReplacement())
            throw new ImmutableReplacement.AlreadyMutatedException();

        if (replacement == null) {
            replacement = createMutableClone();
            replacement.isMutated = false;
            replacement.makeDelegateImmutable();
        }

        replacement.mutationHandler = mutationHandler;


        return replacement;
    }

    abstract ImmutableWrapper<T> createMutableClone();

    abstract void makeDelegateImmutable();


    @DoNotAdvise
    @Override
    public void discardReplacement() {
        replacement = null;
    }

    @DoNotAdvise
    @Override
    public boolean hasReplacement() {
        return replacement != null && replacement.mutationHandler != null;
    }


    // Mutation handling methods

    /**
     * Called if method tried to mutate, so threw UnsupportedOperationException
     */
    @DoNotAdvise
    protected ImmutableWrapper<T> startMutation() {
        if (replacement != null)
            throw new ImmutableReplacement.AlreadyMutatedException();

        replacement = createMutableClone();
        replacement.isMutated = true;

        return replacement;
    }

    /**
     * If method called was a mutation (so 'startMutation()' was called) this is called after the
     * method returns (or throws exception).
     */
    @SuppressWarnings("all")
    @DoNotAdvise
    protected void endMutation() {
        replacement.makeDelegateImmutable();
        mutationHandler.handleReplacement(this, replacement);

        // Check that user either created replacement or forgot it
        if (replacement != null && replacement.mutationHandler == null)
            throw new ReplacementNotHandled();
    }
}

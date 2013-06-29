package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.NewImmutableReplacement;
import BestSoFar.framework.immutables.common.NewMutationHandler;
import lombok.Getter;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 00:17
 */
public abstract class NewImmutableWrapper<T> implements NewImmutableReplacement {

    public static @interface DoNotAdvise {}

    private NewImmutableWrapper<T> replacement;
    private NewMutationHandler mutationHandler;
    @Getter private boolean isMutated;

    // Delegation and setup

    @DoNotAdvise
    @Override
    public NewImmutableWrapper<T> getReplacement() {
        if (hasReplacement())
            return replacement;
        else
            return null;
    }

    /**
     * First-time constructor.  Subsequent objects are created using assignReplacementTo(..).
     * @param mutationHandler
     */
    public NewImmutableWrapper(NewMutationHandler mutationHandler) {
        this.mutationHandler = mutationHandler;
        isMutated = false;
    }

    /**
     * Constructor used when subclass clones.
     */
    public NewImmutableWrapper() {};

    @DoNotAdvise
    @Override
    public NewImmutableWrapper<T> assignReplacementTo(NewMutationHandler mutationHandler) {
        if (hasReplacement())
            throw new NewImmutableReplacement.AlreadyMutatedException();

        if (replacement == null) {
            replacement = createMutableClone();
            replacement.isMutated = false;
            replacement.makeDelegateImmutable();
        }

        replacement.mutationHandler = mutationHandler;


        return replacement;
    }

    abstract NewImmutableWrapper<T> createMutableClone();

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
    protected NewImmutableWrapper<T> startMutation() {
        if (replacement != null)
            throw new NewImmutableReplacement.AlreadyMutatedException();

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

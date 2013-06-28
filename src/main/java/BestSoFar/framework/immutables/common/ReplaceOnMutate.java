package BestSoFar.framework.immutables.common;


/**
 * A class will inherit this if it intends to replace itself when it is notified of a mutation to
 * itself.
 */
public interface ReplaceOnMutate<T> extends MutationHandler {
    /**
     * Returns true iff a replacement has been made of this object.  Used to ensure that only one
     * replacement can be made of any immutable object.
     * @return
     */
    boolean hasReplacement();

    // TODO: having this in messed with AbstractElement and AbstractWorkflowContainer.  fixable?
//    T getReplacement();
}

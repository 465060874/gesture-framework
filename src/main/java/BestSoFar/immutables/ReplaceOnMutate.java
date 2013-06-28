package BestSoFar.immutables;


/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:15
 */
public interface ReplaceOnMutate<T> extends MutationHandler {
    boolean hasReplacement();

    // TODO: having this in messed with AbstractElement and AbstractWorkflowContainer.  fixable?
//    T getReplacement();
}

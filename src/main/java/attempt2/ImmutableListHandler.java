package attempt2;

import attempt2.FailedLists.ImmutableList;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 20:15
 */
public interface ImmutableListHandler<T> {
    void handleMutatedList();
    ImmutableList<T> getContents();
}

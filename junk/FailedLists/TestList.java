package FailedLists;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 12:03
 */
public interface TestList<T> extends List<T> {
    void foo();
    TestList<T> bar(TestList<T> t);
}


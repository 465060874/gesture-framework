package BestSoFar.framework.core.common;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 18:43
 */
public interface ChildOf<T> {
    T getParent();

    void setParent(T parent);
}

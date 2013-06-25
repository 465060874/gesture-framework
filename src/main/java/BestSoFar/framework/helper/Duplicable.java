package BestSoFar.framework.helper;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 16:07
 */
public interface Duplicable<T> {

    /**
     * The most-concrete subclass must implement this method, and return:
     *      return new SubClass(this);
     *
     * @return a copy of this.
     */
    T callCopyConstructor();
}

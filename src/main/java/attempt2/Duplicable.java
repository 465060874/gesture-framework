package attempt2;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 16:07
 */
public interface Duplicable<T> {

    /**
     * Create a duplicate of this object (to be implemented by the lowest subclass,
     * which calls the object's copy constructor which calls all superclasses' copy
     * constructors).
     *
     * @param objectToCopyTo the object to copy data from this into.
     */
    T callCopyConstructor();
}

package attempt1;

/**
 * User: Sam Wright
 * Date: 21/06/2013
 * Time: 02:52
 */
public interface Workflow<I extends DataType, O extends DataType>
        extends Processor<I,O>,
        Iterable<Element<? extends DataType, ? extends DataType>> {

    Workflow<I,O> add(int index, Element<? extends DataType, ? extends DataType> e);

    int size();

    Workflow<I,O> remove(int index);

    Workflow<I, O> remove(Element<? extends DataType, ? extends DataType> e);
}

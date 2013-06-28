package attempt1;

/**
 * User: Sam Wright
 * Date: 21/06/2013
 * Time: 02:36
 */
public interface Processor<I extends DataType, O extends DataType> {
    boolean isValid();
    Mediator<O> process(Mediator<I> input);
    Class<I> getInputType();
    Class<O> getOutputType();
}

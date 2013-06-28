package attempt1;

/**
 * User: Sam Wright
 * Date: 21/06/2013
 * Time: 03:07
 */
public interface WorkflowComposite<I extends DataType, O extends DataType>
        extends Processor<I,O>,
        Iterable<Workflow<I,O>>{

    WorkflowComposite<I, O> add(int index, Workflow<I,O> w);

    int size();

    WorkflowComposite<I, O> remove(int index);

    WorkflowComposite<I, O> remove(Element e);

}

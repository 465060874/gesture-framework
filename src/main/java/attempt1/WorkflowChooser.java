package attempt1;

import java.util.Iterator;

/**
 * User: Sam Wright
 * Date: 21/06/2013
 * Time: 03:14
 */
public class WorkflowChooser<I extends DataType, O extends DataType>
        implements WorkflowComposite<I,O>,
        Trainable<I,O> {
    @Override
    public WorkflowComposite<I, O> add(int index, Workflow<I, O> w) {
        return null; // Dummy implementation
    }

    @Override
    public int size() {
        return 0; // Dummy implementation
    }

    @Override
    public WorkflowComposite<I, O> remove(int index) {
        return null; // Dummy implementation
    }

    @Override
    public WorkflowComposite<I, O> remove(Element e) {
        return null; // Dummy implementation
    }

    @Override
    public Iterator<Workflow<I, O>> iterator() {
        return null; // Dummy implementation
    }

    @Override
    public boolean isValid() {
        return false; // Dummy implementation
    }

    @Override
    public Mediator<O> process(Mediator<I> input) {
        return null; // Dummy implementation
    }

    @Override
    public Class<I> getInputType() {
        return null; // Dummy implementation
    }

    @Override
    public Class<O> getOutputType() {
        return null; // Dummy implementation
    }
}

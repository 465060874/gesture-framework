package attempt2;

import java.util.*;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 12:18
 */
public abstract class AbstractProcessor<I,O> implements Processor<I, O> {

    private final Set<ProcessorObserver<O>> observers = new HashSet<>();

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<I>> inputs) {
        List<Mediator<O>> outputs = new LinkedList<>();

        for (Mediator<I> input : inputs)
            outputs.add(process(input));

        for (ProcessorObserver<O> observer : observers)
            observer.notify(outputs);

        return outputs;
    }

    @Override
    public Map<Mediator<O>, Mediator<I>> mapCompletedTrainingBatchBackward(List<Mediator<O>> completedOutputs,
                                                                           Set<Mediator<O>> successfulOutputs) {

        Map<Mediator<O>, Mediator<I>> mapping = new HashMap<>();

        for (Mediator<O> completedOutput : completedOutputs)
            mapping.put(completedOutput, (Mediator<I>) completedOutput.getPrevious());

        return mapping;
    }


    @Override
    public void addObserver(ProcessorObserver<O> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ProcessorObserver<O> observer) {
        observers.remove(observer);
    }

    @Override
    public Set<ProcessorObserver<O>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }
}

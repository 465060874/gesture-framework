package attempt2;

import java.util.*;

/**
 * Abstract implementor of Processor for elemental Processors to extend, which requires
 * a one-to-one mapping of input data to output data (without training necessary).
 */
public abstract class AbstractElement<I, O> extends ObservableProcessor<I, O> implements Element<I,O> {

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<I>> inputs) {
        List<Mediator<O>> outputs = new LinkedList<>();

        for (Mediator<I> input : inputs)
            outputs.add(process(input));

        for (ProcessorObserver<O> observer : getObservers())
            observer.notify(outputs);

        return outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> mapCompletedTrainingBatchBackward(List<Mediator<O>> completedOutputs,
                                                                           Set<Mediator<O>> successfulOutputs) {

        Map<Mediator<O>, Mediator<I>> mapping = new HashMap<>();

        for (Mediator<O> completedOutput : completedOutputs)
            mapping.put(completedOutput, (Mediator<I>) completedOutput.getPrevious());

        return mapping;
    }


}
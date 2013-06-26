package BestSoFar.framework.core;

import BestSoFar.framework.helper.*;
import BestSoFar.framework.helper.Observable;
import com.sun.istack.internal.NotNull;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Abstract implementor of Processor for elemental Processors to extend, which requires
 * a one-to-one mapping of input data to output data (without training necessary).
 */
public abstract class AbstractElement<I, O> implements Element<I,O> {

    @Delegate
    private final Observable<MediatorObserver<O>> observerHandler = new ObservableImpl<>();

    @Getter @Setter @NotNull private Workflow<?, ?> parent;


    public AbstractElement(Workflow<?, ?> parent, Class<I> inputType, Class<O> outputType) {
        setParent(parent);
        mutatedInputType  = this.inputType = inputType;
        mutatedOutputType = this.outputType = outputType;

    }

    /**
     * Clone constructor.  Subclasses calling this must explicitly state the input and output types.
     * This is one of those oh-so-endearing java generics problems that can't be circumvented.
     *
     * Use '<oldObject.getMutatedInputType() , oldObject.getMutatedOutputType>' as the type.
     *
     * The 'mutated data types' are only different to the old object's data types when the data type
     * mutators have been used.  In practise, this should only be done voluntarily by the concrete element
     * (perhaps a view lets the user choose between a few options).  Feel free to override these
     * mutators and have them throw unchecked exceptions.
     *
     * TODO: should type-mutators be in WorkflowContainer instead? and let type-configurable Elements implement them?
     *
     * @param oldAbstractElement
     */
    public AbstractElement(AbstractElement<?, ?> oldAbstractElement) {
        setParent(oldAbstractElement.getParent());
        mutatedInputType = inputType = (Class<I>) oldAbstractElement.getMutatedInputType();
        mutatedOutputType = outputType = (Class<O>) oldAbstractElement.getMutatedOutputType();
    }

    @Override
    public void setInputType(Class<?> inputType) {
        mutatedInputType = inputType;
    }

    @Override
    public void setOutputType(Class<?> outputType) {
        mutatedOutputType = outputType;
    }

    private void replaceSelf() {
        cloneAs()
    }

    @Override
    public List<Mediator<O>> processTrainingBatch(List<Mediator<I>> inputs) {
        List<Mediator<O>> outputs = new LinkedList<>();

        for (Mediator<I> input : inputs)
            outputs.add(process(input));

        for (MediatorObserver<O> observer : getObservers())
            observer.notify(outputs);

        return outputs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Mediator<O>, Mediator<I>> createBackwardMappingForTrainingBatch(List<Mediator<O>> completedOutputs,
                                                                               Set<Mediator<O>> successfulOutputs) {

        Map<Mediator<O>, Mediator<I>> mapping = new HashMap<>();

        for (Mediator<O> completedOutput : completedOutputs)
            mapping.put(completedOutput, (Mediator<I>) completedOutput.getPrevious());

        return mapping;
    }
}
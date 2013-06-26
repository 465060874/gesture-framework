package BestSoFar.framework.helper;

import BestSoFar.framework.core.Processor;
import BestSoFar.immutables.ReplaceOnMutate;
import lombok.Getter;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 19:22
 */
public class ProcessorMutationHandler<I, O, I2, O2> implements ReplaceOnMutate<Processor<I2, O2>> {

    private final Processor<I, O> original;
    @Getter private Processor<I2, O2> replacement;

    public ProcessorMutationHandler(Processor<I, O> original) {
        this.original = original;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleMutation() {
        replacement = (Processor<I2, O2>) original.cloneAs(original.getTypeData());
        original.replaceSelfWithClone(replacement);
    }

    @Override
    public boolean hasReplacement() {
        return replacement != null;
    }
}

package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.Workflow;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * An object describing the input and output types of {@link Processor} objects.
 */
@AllArgsConstructor()
@EqualsAndHashCode
final public class TypeData<I, O> {
    @Getter @NonNull private final Class<I> inputType;
    @Getter @NonNull private final Class<O> outputType;

    @Override
    public String toString() {
        return new StringBuilder("<")
                .append(inputType.getSimpleName())
                .append(",")
                .append(outputType.getSimpleName())
                .append(">")
                .toString();
    }

    /**
     * Checks if the associated {@link Processor} can do no conversion (ie. its output is its
     * input, without any processing taking place) while still being valid.
     *
     * @return true if the input type can be casted to the output type.
     */
    public boolean canBeEmptyContainer() {
        return outputType.isAssignableFrom(inputType);
    }

    /**
     * Checks if this {@code Processor} can come before the other {@code Processor}.
     *
     * @param other the {@code TypeData} of the other {@code Processor}.
     * @param <I2> the input type of the other {@code Processor}.
     * @param <O2> the output type of the other {@code Processor}.
     * @return true iff this output type can be casted to the other's input type.
     */
    public <I2, O2> boolean canComeBefore(TypeData<I2, O2> other) {
        return other.inputType.isAssignableFrom(this.outputType);
    }

    /**
     * Checks if this {@code Processor} can come after the other {@code Processor}.
     *
     * @param other the {@code TypeData} of the other {@code Processor}.
     * @param <I2> the input type of the other {@code Processor}.
     * @param <O2> the output type of the other {@code Processor}.
     * @return true iff the other's output type can be casted to this input type.
     */
    public <I2, O2> boolean canComeAfter(TypeData<I2, O2> other) {
        return this.inputType.isAssignableFrom(other.outputType);
    }

    /**
     * Checks if this {@code Processor} can be the last element inside the given
     * {@link Workflow workflow}.
     *
     * @param workflow the {@code TypeData} of the {@code Workflow}.
     * @param <I2> the input type of the {@code Workflow}.
     * @param <O2> the output type of the {@code Workflow}.
     * @return true iff this output type can be casted to the workflow's output type.
     */
    public <I2, O2> boolean canBeAtEndOfWorkflow(TypeData<I2, O2> workflow) {
        return workflow.outputType.isAssignableFrom(this.outputType);
    }

    /**
     * Checks if this {@code Processor} can be the first element inside the given
     * {@link Workflow workflow}.
     *
     * @param workflow the {@code TypeData} of the {@code Workflow}.
     * @param <I2> the input type of the {@code Workflow}.
     * @param <O2> the output type of the {@code Workflow}.
     * @return true iff the workflow's input type can be casted to this input type.
     */
    public <I2, O2> boolean canBeAtStartOfWorkflow(TypeData<I2, O2> workflow) {
        return this.inputType.isAssignableFrom(workflow.inputType);
    }
}

package BestSoFar.framework.helper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * User: Sam Wright Date: 25/06/2013 Time: 19:24
 */
@AllArgsConstructor()
@EqualsAndHashCode
public class TypeData<I, O> {
    @Getter @NonNull private final Class<I> inputType;
    @Getter @NonNull private final Class<O> outputType;

    @Override
    public String toString() {
        return "<" + inputType + " , " + outputType + ">";
    }

    public boolean canBeEmptyContainer() {
        return outputType.isAssignableFrom(inputType);
    }

    public <I2, O2> boolean canComeBefore(TypeData<I2, O2> other) {
        return other.inputType.isAssignableFrom(this.outputType);
    }

    public <I2, O2> boolean canComeAfter(TypeData<I2, O2> other) {
        return this.inputType.isAssignableFrom(other.outputType);
    }

    public <I2, O2> boolean canBeAtEndOfContainer(TypeData<I2, O2> container) {
        return container.outputType.isAssignableFrom(this.outputType);
    }

    public <I2, O2> boolean canBeAtStartOfContainer(TypeData<I2, O2> container) {
        return this.inputType.isAssignableFrom(container.inputType);
    }
}

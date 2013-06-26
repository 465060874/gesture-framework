package BestSoFar.immutables;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * User: Sam Wright Date: 25/06/2013 Time: 19:24
 */
@AllArgsConstructor()
@EqualsAndHashCode
public class TypeData<I, O> {
    @Getter @NotNull private final Class<I> inputType;
    @Getter @NotNull private final Class<O> outputType;

    @Override
    public String toString() {
        return "<" + inputType + " , " + outputType + ">";
    }
}

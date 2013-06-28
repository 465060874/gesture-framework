package BestSoFar.framework.immutables.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Value;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 22:58
 */
@AllArgsConstructor
public class BoxImpl<T> implements Box<T> {
    @Getter private T contents;
    @Getter private final boolean mutable;


    @Override
    public void setContents(T contents) {
        if (mutable)
            this.contents = contents;
        else
            throw new UnsupportedOperationException();
    }
}

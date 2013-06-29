package BestSoFar.framework.immutables.helper;

import lombok.Getter;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 22:58
 */
public class BoxImpl<T> implements Box<T> {
    @Getter private T contents;
    private boolean locked = false;

    public BoxImpl(T contents) {
        this.contents = contents;
    }

    @Override
    public void setContents(T contents) {
        if (locked)
            throw new UnsupportedOperationException();

        this.contents = contents;
    }

    @Override
    public void lock() {
        locked = true;
    }
}

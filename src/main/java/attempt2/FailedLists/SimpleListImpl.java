package attempt2.FailedLists;

import lombok.Delegate;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 13:41
 */
public class SimpleListImpl<E> implements SimpleList<E> {
    @Delegate @Getter
    private final List<E> list;

    public SimpleListImpl(List<E> list) {
        this.list = list;
    }
}

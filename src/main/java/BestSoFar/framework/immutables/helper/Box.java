package BestSoFar.framework.immutables.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 22:56
 */
public interface Box<T> {
    T getContents();

    void setContents(T contents);
}

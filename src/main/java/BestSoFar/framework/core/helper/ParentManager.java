package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Workflow;
import BestSoFar.framework.core.common.ChildOf;
import lombok.Getter;

/**
 * User: Sam Wright Date: 05/07/2013 Time: 11:00
 */
public class ParentManager<T> implements ChildOf<T> {
    @Getter private T parent;



    @Override
    public void setParent(T parent) {
        // Dummy implementation
    }
}

package BestSoFar.framework.core;

import BestSoFar.framework.helper.ChildOf;
import BestSoFar.framework.helper.ObservableProcess;

/**
 * An {@code Element} is a {@link Processor} which sits inside a {@link Workflow}.
 * <p/>
 * The parent {@code Workflow} is always up to date - eg. if the parent is replaced (because it
 * was modified) then this object's parent field is updated.  This doesn't affect data already
 * being processed inside this {@code Element} because the data is passed back to the original
 * parent when the {@code process(input)} method returns.
 */
public interface Element<I, O>
        extends Processor<I, O>, ObservableProcess<O>, ChildOf<Workflow<?, ?>> {
}

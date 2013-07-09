package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.EventuallyImmutable;
import BestSoFar.framework.immutables.common.HandledImmutable;
import BestSoFar.framework.immutables.common.ReplacementHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * User: Sam Wright Date: 30/06/2013 Time: 18:00
 */
public abstract class AbstractHandledImmutable implements HandledImmutable {
    @Getter private ReplacementHandler replacementHandler;
    @Getter private boolean mutable;

    public AbstractHandledImmutable(boolean mutable) {
        this.mutable = mutable;
    }

    @Override
    @Synchronized
    final public void assignToHandler(@NonNull ReplacementHandler replacementHandler) {
        if (mutable) {
            finalise();
            mutable = false;
        }
        this.replacementHandler = replacementHandler;
    }

    @Override
    @Synchronized
    final public void proposeReplacement(EventuallyImmutable proposed) {
        replacementHandler.handleReplacement(this, proposed);
    }

}

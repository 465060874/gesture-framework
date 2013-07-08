package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.HandledImmutable;
import BestSoFar.framework.immutables.common.Immutable;
import BestSoFar.framework.immutables.common.MutationHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * User: Sam Wright Date: 30/06/2013 Time: 18:00
 */
public abstract class AbstractHandledImmutable implements HandledImmutable {
    @Getter private MutationHandler mutationHandler;
    @Getter private boolean mutable;

    public AbstractHandledImmutable(boolean mutable) {
        this.mutable = mutable;
    }

    @Override
    @Synchronized
    final public void assignToHandler(@NonNull MutationHandler mutationHandler) {
        if (mutable)
            finalise();
        mutable = false;
        this.mutationHandler = mutationHandler;
    }

    @Override
    @Synchronized
    final public void proposeReplacement(Immutable proposed) {
        mutationHandler.handleReplacement(this, proposed);
    }

}

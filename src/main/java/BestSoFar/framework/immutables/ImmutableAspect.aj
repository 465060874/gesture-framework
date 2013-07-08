package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.HandledImmutable;

/**
 * User: Sam Wright
 * Date: 29/06/2013
 * Time: 10:18
 */
public aspect ImmutableAspect {
    pointcut anyCall(ImmutableWrapper target):
            call(* BestSoFar.framework.immutables.ImmutableWrapper+.*(..)) &&
            ! call(* BestSoFar.framework.immutables.ImmutableWrapper.*(..)) &&
            ! call(@BestSoFar.framework.immutables.ImmutableWrapper.DoNotAdvise * *.*(..)) &&
            ! call(* *.assignReplacementTo(..)) &&
            ! call(* *.createMutableClone(..)) &&
            ! call(* *.freeze(..)) &&
            target(target);

    Object around(ImmutableWrapper target): anyCall(target) {
        Object result = null;

        if (!target.isMutable()) {
            // If target is not frozen, don't advise it.
            result = proceed(target);
        } else {
            // If target is frozen, check for mutations:
            boolean wantsToWrite;

            try {
                result = proceed(target);
                wantsToWrite = false;
            } catch (UnsupportedOperationException e) {
                // If 'proceed(target)' tried to mutate target:
                wantsToWrite = true;
            }

            if (wantsToWrite) {
                // Create a mutable clone of target, and proceed on that
                ImmutableWrapper clone = (ImmutableWrapper) target.createClone(true);
                result = proceed(clone);

                // If mutable clone proceeded without throwing exception,
                // propose it as a replacement:
                target.proposeReplacement(clone);
            }
        }

        return result;
    }
}

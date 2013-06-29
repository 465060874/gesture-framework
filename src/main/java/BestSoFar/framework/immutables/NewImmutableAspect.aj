package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 29/06/2013
 * Time: 10:18
 */
public aspect NewImmutableAspect {
    pointcut anyCall(Object target):
            call(* BestSoFar.framework.immutables.NewImmutableWrapper+.*(..)) &&
            ! call(* BestSoFar.framework.immutables.NewImmutableWrapper.*(..)) &&

            ! call(@BestSoFar.framework.immutables.NewImmutableWrapper.DoNotAdvise * *.*(..)) &&
            ! call(* *.createNewFromMutated(..)) &&
            ! call(* *.cloneDelegateAsMutable(..)) &&
            ! call(* *.makeReplacementFor(..)) &&
            target(target);

    Object around(Object target): anyCall(target) {
        Object result = null;
        boolean wantsToWrite;

        try {
            result = proceed(target);
            wantsToWrite = false;
        } catch (UnsupportedOperationException e) {
            wantsToWrite = true;
        }

        if (wantsToWrite) {
            NewImmutableWrapper wrapper = ((NewImmutableWrapper) target);
            try {
                Object mutable = wrapper.startMutation();
                result = proceed(mutable);
            } finally {
                wrapper.endMutation();
            }
        }

        return result;
    }
}

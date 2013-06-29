package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 28/06/2013
 * Time: 20:50
 */
public aspect ImmutableAspect {
    pointcut anyCall(ImmutableWrapper wrapper):
        call(* BestSoFar.framework.immutables.ImmutableWrapper.*(..)) &&
        ! call(@BestSoFar.framework.immutables.ImmutableWrapper.DoNotAdvise * *.*(..)) &&
        ! call(* *.createNewFromMutated(..)) &&
        ! call(* *.cloneDelegateAsMutable(..)) &&
        ! call(* *.setActiveDelegate(..)) &&
        ! call(* *.makeReplacementFor(..)) &&
        target(wrapper);

    Object around(ImmutableWrapper wrapper): anyCall(wrapper) {
        Object result = null;
        boolean wantsToWrite;

        try {
            wrapper.startRead();
            result = proceed(wrapper);
            wantsToWrite = false;
        } catch (UnsupportedOperationException e) {
            wantsToWrite = true;
        } finally {
            wrapper.endRead();
        }

        if (wantsToWrite) {
            try {
                wrapper.startMutation();
                result = proceed(wrapper);
            } finally {
                wrapper.endMutation();
            }
        }
        return result;
    }
}

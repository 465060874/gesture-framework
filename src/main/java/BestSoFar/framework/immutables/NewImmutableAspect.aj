package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 28/06/2013
 * Time: 20:50
 */
public aspect NewImmutableAspect {
    pointcut anyCall(ImmutableWrapper wrapper):
        call(* (ImmutableWrapper+ && !(ImmutableWrapper || ImmutableReplacement)).*(..)) &&
        ! call(@ImmutableWrapper.DoNotAdvise * *.*(..)) &&
        ! call(* *.createNewFromMutated(..)) &&
        ! call(* *.cloneDelegateAsMutable(..)) &&
        ! call(* *.setActiveDelegate(..)) &&
        target(wrapper);

    Object around(ImmutableWrapper wrapper): anyCall(wrapper) {
        Object result = null;
        boolean wantsToWrite;

        System.out.println("Advising: " + thisJoinPoint);

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

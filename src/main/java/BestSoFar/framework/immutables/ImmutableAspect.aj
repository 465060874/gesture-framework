package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 29/06/2013
 * Time: 10:18
 */
public aspect ImmutableAspect {
    pointcut anyCall(Object target):
            call(* BestSoFar.framework.immutables.ImmutableWrapper+.*(..)) &&
            ! call(* BestSoFar.framework.immutables.ImmutableWrapper.*(..)) &&

            ! call(@BestSoFar.framework.immutables.ImmutableWrapper.DoNotAdvise * *.*(..)) &&
            ! call(* *.makeDelegateImmutable(..)) &&
            ! call(* *.createMutableClone(..)) &&
            ! call(* *.assignReplacementTo(..)) &&
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
            ImmutableWrapper wrapper = ((ImmutableWrapper) target);
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

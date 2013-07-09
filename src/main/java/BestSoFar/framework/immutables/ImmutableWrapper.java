package BestSoFar.framework.immutables;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 00:17
 */
public abstract class ImmutableWrapper extends AbstractHandledImmutable {

    public static @interface DoNotAdvise {}

    /**
     * Constructor used when subclass clones.
     */
    public ImmutableWrapper(boolean mutable) {
        super(mutable);
    }


    @Aspect
    public static class MutationAdvice {
        @Pointcut("call(* BestSoFar.framework.immutables.ImmutableWrapper+.*(..)) &&" +
                "! call(* BestSoFar.framework.immutables.ImmutableWrapper.*(..)) &&" +
                "! call(@BestSoFar.framework.immutables.ImmutableWrapper.DoNotAdvise * *.*(..)) &&" +
                "! call(* *.assignToHandler(..)) &&" +
                "! call(* *.createClone(boolean)) &&" +
                "! call(* *.finalise()) &&" +
                "! call(* *.isMutable()) &&" +
                "! call(* *.proposeReplacement(..)) &&" +
                "target(target)")
        public void callsThatMightMutate(ImmutableWrapper target) {}

        @Around("callsThatMightMutate(target)")
        public Object aroundCalls(ProceedingJoinPoint joinPoint, ImmutableWrapper target) throws Throwable {
            Object result = null;

            if (target.isMutable()) {
                // If target is not frozen, don't advise it.
                result = joinPoint.proceed(new Object[]{joinPoint, target});
            } else {
                // If target is frozen, check for mutations:
                boolean wantsToWrite;
                try {
                    result = joinPoint.proceed(new Object[]{joinPoint, target});
                    wantsToWrite = false;
                } catch (UnsupportedOperationException e) {
                    // If 'proceed(target)' tried to mutate target:
                    wantsToWrite = true;
                }

                if (wantsToWrite) {
                    // Create a mutable clone of target, and proceed on that
                    ImmutableWrapper clone = (ImmutableWrapper) target.createClone(true);
                    result = joinPoint.proceed(new Object[]{joinPoint, clone});

                    // If mutable clone proceeded without throwing exception,
                    // propose it as a replacement:
                    target.proposeReplacement(clone);
                }
            }

            return result;
        }
    }
}

package BestSoFar.framework.immutables;

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


//    @Aspect
//    public static class MutationAdvice {
//        @Pointcut("call(* BestSoFar.framework.immutables.ImmutableWrapper+.*(..)) &&" +
//                "! call(* BestSoFar.framework.immutables.ImmutableWrapper.*(..)) &&" +
//                "! call(@BestSoFar.framework.immutables.ImmutableWrapper.DoNotAdvise * *.*(..)) &&" +
//                "! call(* *.makeDelegateImmutable(..)) &&" +
//                "! call(* *.createMutableClone(..)) &&" +
//                "! call(* *.assignReplacementTo(..)) &&" +
//                "target(target)")
//        public void anyCall(Object target) {}
//
//        @Around("anyCall(target)")
//        public Object aroundCalls(ProceedingJoinPoint joinPoint, Object target) throws Throwable {
//            Object result = null;
//            boolean wantsToWrite;
//
//            System.out.println("Advising " + joinPoint);
//
//            try {
//                result = joinPoint.proceed(new Object[]{target});
//                wantsToWrite = false;
//            } catch (UnsupportedOperationException e) {
//                wantsToWrite = true;
//            }
//
//            if (wantsToWrite) {
//                ImmutableWrapper wrapper = ((ImmutableWrapper) target);
//                try {
//                    Object mutable = wrapper.startMutation();
//                    result = joinPoint.proceed(new Object[]{mutable});
//                } finally {
//                    wrapper.endMutation();
//                }
//            }
//
//            return result;
//        }
//    }
}

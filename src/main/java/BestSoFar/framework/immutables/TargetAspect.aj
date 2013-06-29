package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 28/06/2013
 * Time: 23:56
 */
public aspect TargetAspect {

    pointcut one_foo(Object t):
            call(void TargetTest.say(String)) &&
            target(t);

    void around (Object tS): one_foo(tS) {
        TargetTest t = (TargetTest) tS;
        System.out.println("advice: foo = " + t.getFoo());
        System.out.print("advice: proceed: ");
        proceed(t);
        t = (TargetTest) t.getOneBigger();
        System.out.println("advice: bigger foo = " + t.getFoo());
        System.out.print("advice: proceed: ");
        proceed(t);
    }
}

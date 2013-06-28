package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 28/06/2013
 * Time: 23:56
 */
public aspect TargetAspect {

    pointcut one_foo(TargetTest t):
            call(void TargetTest.say(String)) &&
            target(t);

    void around (TargetTest t): one_foo(t) {
        System.out.println("advice: foo = " + t.getFoo());
        System.out.print("advice: proceed: ");
        proceed(t);
        t = t.getOneBigger();
        System.out.println("advice: bigger foo = " + t.getFoo());
        System.out.print("advice: proceed: ");
        proceed(t);
    }
}

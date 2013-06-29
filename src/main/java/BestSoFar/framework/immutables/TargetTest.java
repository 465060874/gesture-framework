package BestSoFar.framework.immutables;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: Sam Wright Date: 28/06/2013 Time: 23:54
 */
@AllArgsConstructor
public class TargetTest extends SuperTarget {
    @Getter
    private final int foo;

    public void say(String msg) {
        System.out.println(msg + foo);
    }

    public Object getOneBigger() {
        return new TargetTest(foo + 1);
    }

}

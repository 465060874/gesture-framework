package BestSoFar.framework.immutables;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 14:15
 */
public aspect ImmutableListAspect {

    pointcut anyCall(ImmutableListImpl list):
            call(* java.util.List.*(..)) &&
            target(list);

    Object around(ImmutableListImpl list): anyCall(list) {
        Object result = null;
        boolean wantsToWrite;

        try {
            list.startRead();
            result = proceed(list);
            wantsToWrite = false;
        } catch (UnsupportedOperationException e) {
            wantsToWrite = true;
        } finally {
            list.endRead();
        }

        if (wantsToWrite) {
            try {
                list.startMutation();
                result = proceed(list);
            } finally {
                list.endMutation();
            }
        }

        return result;
    }

}

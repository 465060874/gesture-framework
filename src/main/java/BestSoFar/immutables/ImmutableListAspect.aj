package BestSoFar.immutables;

/**
 * User: Sam Wright
 * Date: 25/06/2013
 * Time: 14:15
 */
public aspect ImmutableListAspect {

    pointcut anyCall(ImmutableList list):
            call(* BestSoFar.immutables.ImmutableListImpl.*(..)) &&
            target(list);

    Object around(ImmutableListImpl list): anyCall(list) {
        Object result;

        try {
            result = proceed(list);
        } catch (UnsupportedOperationException e) {
            list.makeMutable();
            result = proceed(list);
            list.notifyMutationHandler();
        }

        return result;
    }

}

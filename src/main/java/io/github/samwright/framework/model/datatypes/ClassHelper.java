package io.github.samwright.framework.model.datatypes;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Taken nearly verbatim from this user: http://stackoverflow.com/users/898289/adam
 * from this website:
 * http://stackoverflow.com/questions/9797212/finding-the-nearest-common-superclass-or-superinterface-of-a-collection-of-cla
 */
public class ClassHelper {

    public static Class lowestCommonAncestor(List<Class> classes) {
        LinkedHashSet<Class> sharedAncestry = null;

        for (Class clazz : classes) {
            LinkedHashSet<Class> ancestry = getAncestry(clazz);

            if (sharedAncestry == null)
                sharedAncestry = ancestry;
            else
                sharedAncestry.retainAll(ancestry);
        }

        if (sharedAncestry == null || sharedAncestry.isEmpty())
            return Object.class;
        else
            return sharedAncestry.iterator().next();
    }

    /**
     *
     *
     * @param clazz
     * @return
     */
    public static LinkedHashSet<Class> getAncestry(Class clazz) {
        LinkedHashSet<Class> ancestry = new LinkedHashSet<>();
        LinkedHashSet<Class> nextLevel = new LinkedHashSet<>();
        nextLevel.add(clazz);

        do {
            ancestry.addAll(nextLevel);
            Set<Class> thisLevel = new LinkedHashSet<>(nextLevel);
            nextLevel.clear();

            for (Class clazzInThisLevel : thisLevel) {
                Class superClazz = clazzInThisLevel.getSuperclass();

                if (superClazz != null && superClazz != Object.class)
                    nextLevel.add(superClazz);

                Collections.addAll(nextLevel, clazzInThisLevel.getInterfaces());
            }
        } while(!nextLevel.isEmpty());

        return ancestry;
    }

}

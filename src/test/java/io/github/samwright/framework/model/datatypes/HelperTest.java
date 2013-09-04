package io.github.samwright.framework.model.datatypes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 21:02
 */
public class HelperTest {

    interface A {}
    interface B {}
    class C {}

    interface AB extends A, B {}
    class AC extends C implements A {}
    class ABC extends C implements AB {}
    class BC extends C implements B {}
    class BCA extends BC implements A {}


    @Test
    public void testLowestCommonAncestor() throws Exception {
        assertEquals(C.class, Helper.lowestCommonAncestor(listOf(AC.class, ABC.class)));
        assertEquals(A.class, Helper.lowestCommonAncestor(listOf(AC.class, AB.class)));
        assertEquals(C.class, Helper.lowestCommonAncestor(listOf(BC.class, AC.class)));
        assertEquals(C.class, Helper.lowestCommonAncestor(listOf(BC.class, ABC.class)));
        assertEquals(A.class, Helper.lowestCommonAncestor(listOf(BCA.class, AB.class)));
    }

    @Test
    public void testGetAncestry() throws Exception {
        assertEquals(setOf(A.class), Helper.getAncestry(A.class));
        assertEquals(setOf(AB.class, A.class, B.class), Helper.getAncestry(AB.class));
        assertEquals(setOf(AC.class, C.class, A.class), Helper.getAncestry(AC.class));
        assertEquals(setOf(ABC.class, C.class, AB.class, A.class, B.class),
                Helper.getAncestry(ABC.class));
        assertEquals(setOf(BC.class, C.class, B.class), Helper.getAncestry(BC.class));
        assertEquals(setOf(BCA.class, BC.class, C.class, B.class, A.class),
                Helper.getAncestry(BCA.class));
    }

    private LinkedHashSet<Class> setOf(Class... children) {
        return new LinkedHashSet<>(listOf(children));
    }

    private List<Class> listOf(Class... children) {
        return new ArrayList<>(Arrays.asList(children));
    }
}

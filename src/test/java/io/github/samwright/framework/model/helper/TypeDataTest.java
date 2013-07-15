package io.github.samwright.framework.model.helper;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * User: Sam Wright Date: 15/07/2013 Time: 13:06
 */
public class TypeDataTest {
    private TypeData<Integer, Integer> intToInt;
    private TypeData<Integer, String> intToString;
    private TypeData<String, Integer> stringToInt;
    private TypeData<String, String> stringToString;

    @Before
    public void setUp() throws Exception {
        intToInt = new TypeData<>(Integer.class, Integer.class);
        intToString = new TypeData<>(Integer.class, String.class);
        stringToInt = new TypeData<>(String.class, Integer.class);
        stringToString = new TypeData<>(String.class, String.class);
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("<Integer,Integer>", intToInt.toString());
    }

    @Test
    public void testCanBeEmptyContainer() throws Exception {
        assertTrue(intToInt.canBeEmptyContainer());
        assertTrue(stringToString.canBeEmptyContainer());
        assertFalse(stringToInt.canBeEmptyContainer());
        assertFalse(intToString.canBeEmptyContainer());
    }

    @Test
    public void testCanComeBefore() throws Exception {
        assertTrue(intToInt.canComeBefore(intToString));
        assertTrue(stringToInt.canComeBefore(intToString));
        assertTrue(intToInt.canComeBefore(intToInt));
        assertTrue(stringToInt.canComeBefore(intToInt));

        assertFalse(intToInt.canComeBefore(stringToInt));
        assertFalse(intToInt.canComeBefore(stringToString));
        assertFalse(stringToInt.canComeBefore(stringToInt));
        assertFalse(stringToInt.canComeBefore(stringToString));
    }

    @Test
    public void testCanComeAfter() throws Exception {
        assertTrue(intToString.canComeAfter(intToInt));
        assertTrue(intToString.canComeAfter(stringToInt));
        assertTrue(intToInt.canComeAfter(intToInt));
        assertTrue(intToInt.canComeAfter(stringToInt));

        assertFalse(stringToInt.canComeAfter(intToInt));
        assertFalse(stringToString.canComeAfter(intToInt));
        assertFalse(stringToInt.canComeAfter(stringToInt));
        assertFalse(stringToString.canComeAfter(stringToInt));
    }

    @Test
    public void testCanBeAtEndOfWorkflow() throws Exception {
        assertTrue(intToInt.canBeAtEndOfWorkflow(intToInt));
        assertTrue(intToInt.canBeAtEndOfWorkflow(stringToInt));
        assertTrue(stringToInt.canBeAtEndOfWorkflow(intToInt));
        assertTrue(stringToInt.canBeAtEndOfWorkflow(stringToInt));

        assertFalse(intToString.canBeAtEndOfWorkflow(intToInt));
        assertFalse(intToString.canBeAtEndOfWorkflow(stringToInt));
        assertFalse(stringToString.canBeAtEndOfWorkflow(intToInt));
        assertFalse(stringToString.canBeAtEndOfWorkflow(stringToInt));
    }

    @Test
    public void testCanBeAtStartOfWorkflow() throws Exception {
        assertTrue(intToInt.canBeAtStartOfWorkflow(intToInt));
        assertTrue(intToInt.canBeAtStartOfWorkflow(intToString));
        assertTrue(intToString.canBeAtStartOfWorkflow(intToInt));
        assertTrue(intToString.canBeAtStartOfWorkflow(intToString));

        assertFalse(stringToInt.canBeAtStartOfWorkflow(intToInt));
        assertFalse(stringToInt.canBeAtStartOfWorkflow(intToString));
        assertFalse(stringToString.canBeAtStartOfWorkflow(intToInt));
        assertFalse(stringToString.canBeAtStartOfWorkflow(intToString));
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(intToInt, intToInt);
        assertFalse(intToInt.equals(intToString));
        assertFalse(intToInt.equals(stringToInt));
        assertFalse(intToInt.equals(stringToString));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(intToInt.hashCode(), intToInt.hashCode());
        assertFalse(intToInt.hashCode() == intToString.hashCode());
        assertFalse(intToInt.hashCode() == stringToInt.hashCode());
        assertFalse(intToInt.hashCode() == stringToString.hashCode());
    }

    @Test
    public void testGetInputType() throws Exception {
        assertEquals(Integer.class, intToString.getInputType());
    }

    @Test
    public void testGetOutputType() throws Exception {
        assertEquals(String.class, intToString.getOutputType());
    }
}

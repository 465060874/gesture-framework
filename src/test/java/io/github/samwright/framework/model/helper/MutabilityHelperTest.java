package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.helper.mock.MockProcessor;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.*;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 17:16
 */
public class MutabilityHelperTest {
    private MockProcessor first, second, third;

    @Before
    public void setUp() throws Exception {
        first = new MockProcessor();
    }

    @Test
    public void testCreateMutableClone() throws Exception {
        second = first.createMutableClone();

        assertTrue(second.isMutable());
        assertNull(second.getPrevious());
        assertNull(second.getNext());
    }

    @Test
    public void testVersionInfo() throws Exception {
        assertFalse(first.isMutable());
        assertNull(first.getPrevious());
        assertNull(first.getNext());
    }

    @Test(expected = RuntimeException.class)
    public void testIneffectualReplace() throws Exception {
        second = first.createMutableClone();
        second.replace(second);

        assertFalse(second.isMutable());
        assertNull(second.getPrevious());
        assertNull(second.getNext());
    }

    @Test
    public void testReplace() throws Exception {
        second = first.createMutableClone();
        second.replace(first);

        assertFalse(second.isMutable());
        assertEquals(first, second.getPrevious());
        assertEquals(second, first.getNext());
        assertNull(second.getNext());
    }

    @Test
    public void testReplaceWith() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);

        assertFalse(first.isMutable());
        assertNull(first.getPrevious());
        assertEquals(second, first.getNext());

        assertFalse(second.isMutable());
        assertEquals(first, second.getPrevious());
        assertNull(second.getNext());
    }

    @Test(expected = RuntimeException.class)
    public void testCannotReplaceMutable() throws Exception {
        second = first.createMutableClone();
        third = second.createMutableClone();
        second.replaceWith(third);
    }

    @Test
    public void testCanReplaceAlreadyReplaced() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        first.replaceWith(third);
    }

    @Test(expected = RuntimeException.class)
    public void testReplacementCannotHavePrevious() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        second.replaceWith(third);

        first.discardNext();

        first.replaceWith(third);
    }

    @Test(expected = RuntimeException.class)
    public void testCannotReplaceSelf() throws Exception {
        first.replaceWith(first);
    }

    @Test
    public void testDiscardNext() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        second.replaceWith(third);

        first.discardNext();

        assertFalse(first.isMutable());
        assertNull(first.getPrevious());
        assertNull(first.getNext());

        assertFalse(second.isMutable());
        assertNull(second.getPrevious());
        assertEquals(third, second.getNext());
    }

    @Test
    public void testShortenSequence() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        second.replaceWith(third);

        first.discardNext();
        third.discardPrevious();

        first.replaceWith(third);

        assertFalse(first.isMutable());
        assertNull(first.getPrevious());
        assertEquals(third, first.getNext());

        assertFalse(third.isMutable());
        assertEquals(first, third.getPrevious());
        assertNull(third.getNext());

        assertFalse(second.isMutable());
        assertNull(second.getPrevious());
        assertNull(second.getNext());
    }

    @Test
    public void testDiscardPrevious() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        second.replaceWith(third);

        second.discardPrevious();

        assertFalse(first.isMutable());
        assertNull(first.getPrevious());
        assertNull(first.getNext());

        assertFalse(second.isMutable());
        assertNull(second.getPrevious());
        assertEquals(third, second.getNext());
    }

    @Test
    public void testCanReplaceDeleted() throws Exception {
        second = first.createMutableClone();
        first.delete();
        first.replaceWith(second);
    }

    @Test
    public void testCanDeleteDeleted() throws Exception {
        first.delete();
        first.delete();
    }

    @Test
    public void testCanDeleteReplaced() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        first.delete();
        assertNotSame(second, first.getNext());
    }

    @Test
    public void testCanDiscardDeletion() throws Exception {
        first.delete();
        first.discardNext();
        first.replaceWith(first.createMutableClone());
    }
}

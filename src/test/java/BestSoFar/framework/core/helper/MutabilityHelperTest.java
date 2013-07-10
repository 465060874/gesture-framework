package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.helper.mock.MockEventuallyImmutable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 17:16
 */
public class MutabilityHelperTest {
    private MockEventuallyImmutable first, second, third;
    private MutabilityHelper mutabilityHelper;

    @Before
    public void setUp() throws Exception {
        first = new MockEventuallyImmutable();
        mutabilityHelper = first.getMutabilityHelper();
    }

    @Test
    public void testCreateMutableClone() throws Exception {
        second = first.createMutableClone();

        assertFalse(second.isDeleted());
        assertTrue(second.isMutable());
        assertNull(second.versionInfo().getPrevious());
        assertNull(second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test
    public void testVersionInfo() throws Exception {
        assertFalse(first.isDeleted());
        assertFalse(first.isMutable());
        assertNull(first.versionInfo().getPrevious());
        assertNull(first.versionInfo().getNext());
        assertEquals(first, first.versionInfo().getThisVersion());
    }

    @Test
    public void testIneffectualFixAsVersion() throws Exception {
        second = first.createMutableClone();
        second.fixAsVersion(second.versionInfo());

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertNull(second.versionInfo().getPrevious());
        assertNull(second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test
    public void testFixAsVersion() throws Exception {
        second = first.createMutableClone();
        second.fixAsVersion(second.versionInfo().withPrevious(first));

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertEquals(first, second.versionInfo().getPrevious());
        assertNull(second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test
    public void testReplaceWith() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);

        assertFalse(first.isDeleted());
        assertFalse(first.isMutable());
        assertNull(first.versionInfo().getPrevious());
        assertEquals(second, first.versionInfo().getNext());
        assertEquals(first, first.versionInfo().getThisVersion());

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertEquals(first, second.versionInfo().getPrevious());
        assertNull(second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test(expected = RuntimeException.class)
    public void testCannotReplaceMutable() throws Exception {
        second = first.createMutableClone();
        third = second.createMutableClone();
        second.replaceWith(third);
    }

    @Test(expected = RuntimeException.class)
    public void testCannotReplaceAlreadyReplaced() throws Exception {
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

        assertFalse(first.isDeleted());
        assertFalse(first.isMutable());
        assertNull(first.versionInfo().getPrevious());
        assertNull(first.versionInfo().getNext());
        assertEquals(first, first.versionInfo().getThisVersion());

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertNull(second.versionInfo().getPrevious());
        assertEquals(third, second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
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

        assertFalse(first.isDeleted());
        assertFalse(first.isMutable());
        assertNull(first.versionInfo().getPrevious());
        assertEquals(third, first.versionInfo().getNext());
        assertEquals(first, first.versionInfo().getThisVersion());

        assertFalse(third.isDeleted());
        assertFalse(third.isMutable());
        assertEquals(first, third.versionInfo().getPrevious());
        assertNull(third.versionInfo().getNext());
        assertEquals(third, third.versionInfo().getThisVersion());

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertNull(second.versionInfo().getPrevious());
        assertNull(second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test
    public void testDiscardPrevious() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        third = second.createMutableClone();
        second.replaceWith(third);

        second.discardPrevious();

        assertFalse(first.isDeleted());
        assertFalse(first.isMutable());
        assertNull(first.versionInfo().getPrevious());
        assertNull(first.versionInfo().getNext());
        assertEquals(first, first.versionInfo().getThisVersion());

        assertFalse(second.isDeleted());
        assertFalse(second.isMutable());
        assertNull(second.versionInfo().getPrevious());
        assertEquals(third, second.versionInfo().getNext());
        assertEquals(second, second.versionInfo().getThisVersion());
    }

    @Test
    public void testCanDeleteFirst() throws Exception {
        first.delete();

        assertTrue(first.isDeleted());
    }

    @Test
    public void testCanDeleteLast() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        second.delete();

        assertFalse(first.isDeleted());
        assertTrue(second.isDeleted());
    }

    @Test(expected = RuntimeException.class)
    public void testCannotReplaceDeleted() throws Exception {
        second = first.createMutableClone();
        first.delete();
        first.replaceWith(second);
    }

    @Test(expected = RuntimeException.class)
    public void testCannotDeleteDeleted() throws Exception {
        first.delete();
        first.delete();
    }

    @Test(expected = RuntimeException.class)
    public void testCannotDeleteReplaced() throws Exception {
        second = first.createMutableClone();
        first.replaceWith(second);
        first.delete();
    }

    @Test
    public void testCanDiscardDeletion() throws Exception {
        first.delete();
        first.discardNext();
        assertFalse(first.isDeleted());
        first.replaceWith(first.createMutableClone());
    }
}

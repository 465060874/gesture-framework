package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.ImmutableReplacement;
import BestSoFar.framework.immutables.common.ReplaceOnMutate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * User: Sam Wright Date: 27/06/2013 Time: 11:32
 */
public class ImmutableListImplTest {
    class TestHandler implements ReplaceOnMutate<TestHandler> {
        private ImmutableListImpl<String> list;

        public TestHandler() {
            list = new ImmutableListImpl<>(this);
        }

        public ImmutableListImpl<String> getList() {
            return list;
        }

        @Override
        public void handleMutation() {
            hasBeenNotified = true;
            list = (ImmutableListImpl<String>) list.makeReplacementFor(this);
        }

        @Override
        public boolean hasReplacement() {
            return false; // Dummy implementation
        }
    }

    private boolean hasBeenNotified = false;
    private ImmutableListImpl<String> list;
    private TestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        list = handler.getList();
    }

    @Test
    public void testMutability() throws Exception {
        list.add("Hello");
        assertTrue(list.isEmpty());
        assertEquals(Arrays.asList("Hello"), handler.getList());
    }

    @Test
    public void testWriteLock() throws Exception {
        list.startMutation();
        list.endMutation();
    }

    @Test
    public void testReadLock() throws Exception {
        list.startRead();
        list.endRead();
    }

    @Test
    public void testNotifyMutationHandler() throws Exception {
        assertFalse(hasBeenNotified);
        list.size(); // read-only operations don't notify handler
        assertFalse(hasBeenNotified);
        list.add("hello"); // write-only ops will notify handler
        assertTrue(hasBeenNotified);
    }

    @Test
    public void testUnmodifiedHandling() throws Exception {
        list.addAll(Arrays.asList("hello", "goodbye"));
        list = handler.getList();

        ImmutableList<String> replacementList = list.makeReplacementFor(handler);

        assertEquals(replacementList, list);
        assertTrue(list.getReplacement() == replacementList);
        assertTrue(list.hasReplacement());
        assertFalse(list.replacementIsMutated());
    }

    @Test
    public void testForgetReplacement() throws Exception {
        testUnmodifiedHandling();

        list.forgetReplacement();

        assertFalse(list.hasReplacement());
        assertFalse(list.replacementIsMutated());
        assertNull(list.getReplacement());
    }

    @Test(expected = ImmutableReplacement.AlreadyMutatedException.class)
    public void testMutationAfterReplacement() throws Exception {
        testUnmodifiedHandling();
        list.add("should fail");
    }

    @Test
    public void testModifiedHandling() throws Exception {
        list.addAll(Arrays.asList("hello", "goodbye"));
        list = handler.getList();
        list.add("third");
        ImmutableList<String> replacementList = handler.getList();

        assertEquals(Arrays.asList("hello", "goodbye"), list);
        assertEquals(Arrays.asList("hello", "goodbye", "third"), replacementList);

        assertTrue(list.getReplacement() == replacementList);
        assertTrue(list.hasReplacement());
        assertTrue(list.replacementIsMutated());
    }

    @Test
    public void testForgetModifiedReplacement() throws Exception {
        testModifiedHandling();

        list.forgetReplacement();

        assertFalse(list.hasReplacement());
        assertFalse(list.replacementIsMutated());
        assertNull(list.getReplacement());
    }

    @Test(expected = ImmutableReplacement.AlreadyMutatedException.class)
    public void testSecondMutation() throws Exception {
        testModifiedHandling();
        list.add("should fail");
    }
}

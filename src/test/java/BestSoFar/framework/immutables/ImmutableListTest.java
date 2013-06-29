package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.ImmutableReplacement;
import BestSoFar.framework.immutables.common.MutationHandler;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;

/**
 * User: Sam Wright Date: 29/06/2013 Time: 10:30
 */
public class ImmutableListTest {
    class TestHandler implements MutationHandler {
        private ImmutableList<String> handledList;
        private boolean willAssignReplacement = true;
        private boolean willForgetReplacement = false;

        public TestHandler() {
            handledList = new ImmutableList<>(this);
        }

        public ImmutableList<String> getHandledList() {
            return handledList;
        }

        @Override
        public void handleReplacement(ImmutableReplacement existingObject,
                                      ImmutableReplacement proposedReplacement) {
            hasBeenNotified = true;
            if (willAssignReplacement)
                handledList = (ImmutableList<String>) existingObject.assignReplacementTo(this);
            if (willForgetReplacement)
                handledList.discardReplacement();

        }
    }

    private boolean hasBeenNotified = false;
    private ImmutableList<String> list;
    private TestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        list = handler.getHandledList();
    }

    @Test
    public void testMutability() throws Exception {
        list.add("Hello");
        assertTrue(list.isEmpty());
        assertEquals(Arrays.asList("Hello"), handler.getHandledList());
    }

    @Test
    public void testWriteLock() throws Exception {
        list.startMutation();
        list.endMutation();
        assertTrue(hasBeenNotified);
    }

    @Test
    public void testNotifyMutationHandler() throws Exception {
        assertFalse(hasBeenNotified);
        list.size(); // read-only operations don't notify handler
        assertFalse(hasBeenNotified);
        list.add("hello"); // mutation operations will notify handler
        assertTrue(hasBeenNotified);
    }

    @Test
    public void testUnmodifiedHandling() throws Exception {
        list.addAll(Arrays.asList("hello", "goodbye"));
        list = handler.getHandledList();

        ImmutableList<String> replacementList = list.assignReplacementTo(handler);

        assertEquals(replacementList, list);
        assertTrue(list.getReplacement() == replacementList);
        assertTrue(list.hasReplacement());
        assertFalse(replacementList.isMutated());
    }

    @Test
    public void testForgetReplacement() throws Exception {
        testUnmodifiedHandling();

        list.discardReplacement();

        assertFalse(list.hasReplacement());
        assertNull(list.getReplacement());

        // I can mutate again without getting exception
        list.add("boo");
    }

    @Test(expected = ImmutableReplacement.AlreadyMutatedException.class)
    public void testMutationAfterReplacement() throws Exception {
        testUnmodifiedHandling();
        list.add("should fail");
    }

    @Test
    public void testModifiedHandling() throws Exception {
        list.addAll(Arrays.asList("hello", "goodbye"));
        list = handler.getHandledList();
        list.add("third");
        ImmutableList<String> replacementList = handler.getHandledList();

        assertEquals(Arrays.asList("hello", "goodbye"), list);
        assertEquals(Arrays.asList("hello", "goodbye", "third"), replacementList);

        assertTrue(list.getReplacement() == replacementList);
        assertTrue(list.hasReplacement());
        assertTrue(replacementList.isMutated());
    }

    @Test
    public void testForgetModifiedReplacement() throws Exception {
        testModifiedHandling();

        list.discardReplacement();

        assertFalse(list.hasReplacement());
        assertNull(list.getReplacement());

        // I can mutate again without getting exception
        list.add("boo");
    }

    @Test(expected = ImmutableReplacement.AlreadyMutatedException.class)
    public void testSecondMutation() throws Exception {
        testModifiedHandling();
        list.add("should fail");
    }
}

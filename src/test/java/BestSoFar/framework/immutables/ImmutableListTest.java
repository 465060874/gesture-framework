package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.HandledImmutable;
import BestSoFar.framework.immutables.common.Immutable;
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
//        public boolean willAssignReplacement = true;
//        public boolean willForgetReplacement = false;

        public TestHandler() {
            handledList = new ImmutableList<>(false);
            handledList.assignToHandler(this);
        }

        public ImmutableList<String> getHandledList() {
            return handledList;
        }

        @Override
        public void handleReplacement(Immutable existingObject, Immutable proposedObject) {
            hasBeenNotified = true;
            handledList = (ImmutableList<String>) proposedObject;
        }

        public void finalise() {
            handledList.assignToHandler(this);
        }
    }

    private boolean hasBeenNotified = false;
    private ImmutableList<String> list, anotherList;
    private TestHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        list = handler.getHandledList();
        anotherList = new ImmutableList<>(true);
        anotherList.addAll(Arrays.asList("a1", "b2"));
    }

    @Test
    public void testMutability() throws Exception {
        list.add("Hello");
        assertTrue(list.isEmpty());
        assertEquals(Arrays.asList("Hello"), handler.getHandledList());
    }

    @Test
    public void testWriteLock() throws Exception {
        list.proposeReplacement(anotherList);
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
    public void testWhileStillMutable() throws Exception {
        list.addAll(Arrays.asList("hello", "goodbye"));
        handler.finalise();
        list = handler.getHandledList();


        ImmutableList<String> replacementList = list.createClone(false);

        assertFalse(list.isMutable());
        assertTrue(replacementList.isMutable());
        assertEquals(list, replacementList);
        assertFalse(hasBeenNotified);

        list.proposeReplacement(replacementList);

        assertTrue(replacementList == handler.getHandledList());
        assertTrue(hasBeenNotified);
        assertFalse(list.isMutable());
        assertFalse(replacementList.isMutable());
    }

//    @Test
//    public void testForgetReplacement() throws Exception {
//        testWhileStillMutable();
//
//        list.discard();
//
//        assertFalse(list.hasReplacement());
//        assertNull(list.getReplacement());
//
//        // I can mutate again without getting exception
//        list.add("boo");
//    }
//
//    @Test(expected = HandledImmutable.AlreadyMutatedException.class)
//    public void testMutationAfterReplacement() throws Exception {
//        testUnmodifiedHandling();
//        list.add("should fail");
//    }
//
//    @Test
//    public void testModifiedHandling() throws Exception {
//        list.addAll(Arrays.asList("hello", "goodbye"));
//        list = handler.getHandledList();
//        list.add("third");
//        ImmutableList<String> replacementList = handler.getHandledList();
//
//        assertEquals(Arrays.asList("hello", "goodbye"), list);
//        assertEquals(Arrays.asList("hello", "goodbye", "third"), replacementList);
//
//        assertTrue(list.getReplacement() == replacementList);
//        assertTrue(list.hasReplacement());
//        assertTrue(replacementList.isMutated());
//    }
//
//    @Test
//    public void testForgetModifiedReplacement() throws Exception {
//        testModifiedHandling();
//
//        list.discard();
//
//        assertFalse(list.hasReplacement());
//        assertNull(list.getReplacement());
//
//        // I can mutate again without getting exception
//        list.add("boo");
//    }
//
//    @Test(expected = HandledImmutable.AlreadyMutatedException.class)
//    public void testSecondMutation() throws Exception {
//        testModifiedHandling();
//        list.add("should fail");
//    }
//
//    @Test
//    public void testHandlerDiscardsReplacment() throws Exception {
//        handler.willAssignReplacement = false;
//        handler.willForgetReplacement = true;
//        list.add("will be discarded");
//
//        assertTrue(handler.getHandledList().isEmpty());
//    }
//
//    @Test(expected = HandledImmutable.ReplacementNotHandledException.class)
//    public void testUnhandledReplacement() throws Exception {
//        handler.willAssignReplacement = false;
//        handler.willForgetReplacement = false;
//        list.add("won't be handled");
//    }
}

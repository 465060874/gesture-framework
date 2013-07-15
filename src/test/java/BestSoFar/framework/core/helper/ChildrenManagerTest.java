package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.helper.mock.MockImmutableParentChild;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: Sam Wright Date: 12/07/2013 Time: 13:01
 */
public class ChildrenManagerTest {
    private MockImmutableParentChild parent;
    private MockImmutableParentChild child1, child2;

    @Before
    public void setUp() throws Exception {
        child1 = new MockImmutableParentChild();
        child2 = new MockImmutableParentChild();
        parent = new MockImmutableParentChild();
    }

    private void updateToNext() {
        MockImmutableParentChild newParent, newChild1, newChild2;

        if (parent != null) {
            newParent = (MockImmutableParentChild) parent.versionInfo().getNext();
            assertEquals(parent.versionInfo().getNext(), newParent);
            assertNull(newParent.versionInfo().getNext());
        } else {
            newParent = null;
        }

        if (child1 != null) {
            newChild1 = (MockImmutableParentChild) child1.versionInfo().getNext();
            assertEquals(child1.versionInfo().getNext(), newChild1);
            assertNull(newChild1.versionInfo().getNext());
        } else {
            newChild1 = null;
        }

        if (child2 != null) {
            newChild2 = (MockImmutableParentChild) child2.versionInfo().getNext();
            assertEquals(child2.versionInfo().getNext(), newChild2);
            assertNull(newChild2.versionInfo().getNext());
        } else {
            newChild2 = null;
        }

        parent = newParent;
        child1 = newChild1;
        child2 = newChild2;
    }

    @Test
    public void testWithChildren() throws Exception {
        MockImmutableParentChild newParent = parent.withChildren(Arrays.asList(child1, child2));
        parent.replaceWith(newParent);
        updateToNext();

        assertEquals(parent, child1.getParent());
        assertEquals(parent, child2.getParent());
        assertEquals(Arrays.asList(child1, child2), parent.getChildren());

    }

    @Test
    public void testDeleteChild() throws Exception {
        testWithChildren();
        child2.delete();
        MockImmutableParentChild deletedChild = child2;
        child2 = null;
        updateToNext();

        assertEquals(Arrays.asList(child1), parent.getChildren());
        assertTrue(deletedChild.isDeleted());

    }

    @Test
    public void testFixAsVersion() throws Exception {

    }

    @Test
    public void testDiscardNext() throws Exception {

    }

    @Test
    public void testDiscardPrevious() throws Exception {

    }

    @Test
    public void testGetChildren() throws Exception {

    }
}

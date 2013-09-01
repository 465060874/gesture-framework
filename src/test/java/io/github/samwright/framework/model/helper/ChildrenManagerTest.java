package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.helper.mock.MockImmutableParentChild;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;


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

    @Test
    public void testWithChildren() throws Exception {
        MockImmutableParentChild newParent = parent.withChildren(Arrays.asList(child1, child2));
        parent.replaceWith(newParent);

        MockImmutableParentChild newChild1, newChild2;

        assertNull(newParent.getNext());

        newChild1 = (MockImmutableParentChild) child1.getNext();
        assertNull(newChild1.getNext());

        newChild2 = (MockImmutableParentChild) child2.getNext();
        assertNull(newChild2.getNext());

        assertEquals(newParent, newChild1.getParent());
        assertEquals(newParent, newChild2.getParent());
        assertEquals(Arrays.asList(newChild1, newChild2), newParent.getChildren());

        child1 = newChild1;
        child2 = newChild2;
        parent = newParent;
    }

    @Test
    public void testDiscardNext() throws Exception {
        testWithChildren();

        MockImmutableParentChild newChild1 = child1.withParent(null);
        child1.replaceWith(newChild1);

        parent.discardNext();
        assertNull(parent.getNext());
        assertNull(child1.getNext());
        assertNull(child2.getNext());
    }

    @Test
    public void testDiscardPrevious() throws Exception {
        testWithChildren();

        parent.discardPrevious();
        assertNull(parent.getPrevious());
        assertNull(child1.getPrevious());
        assertNull(child2.getPrevious());
    }
}

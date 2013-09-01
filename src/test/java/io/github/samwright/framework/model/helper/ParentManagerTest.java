package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.helper.mock.MockImmutableParentChild;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.*;

/**
 * User: Sam Wright Date: 15/07/2013 Time: 09:24
 */
public class ParentManagerTest {
    private MockImmutableParentChild parent;
    private MockImmutableParentChild child1, child2;

    @Before
    public void setUp() throws Exception {
        parent = new MockImmutableParentChild();
        child1 = new MockImmutableParentChild();
        child2 = new MockImmutableParentChild();
    }

    @Test
    public void testWithParent() throws Exception {
        MockImmutableParentChild newChild1, newParent;

        newChild1 = child1.withParent(parent);
        child1.replaceWith(newChild1);

        newParent = (MockImmutableParentChild) parent.getNext();

        assertNull(newParent.getNext());
        assertNull(newChild1.getNext());

        assertEquals(newParent, newChild1.getParent());
        assertEquals(Arrays.asList(newChild1), newParent.getChildren());

        child1 = newChild1;
        parent = newParent;
    }

    @Test
    public void testWithParentBothChildren() throws Exception {
        testWithParent();

        MockImmutableParentChild newChild1, newChild2, newParent;

        newChild2 = child2.withParent(parent);
        child2.replaceWith(newChild2);

        newChild1 = (MockImmutableParentChild) child1.getNext();
        newParent = (MockImmutableParentChild) parent.getNext();

        assertNull(newChild1.getNext());
        assertNull(newChild2.getNext());
        assertNull(newParent.getNext());

        assertEquals(newParent, newChild2.getParent());
        assertEquals(newParent, newChild1.getParent());
        assertEquals(Arrays.asList(newChild1, newChild2), newParent.getChildren());

        child1 = newChild1;
        child2 = newChild2;
        parent = newParent;
    }

    @Test
    public void testWithSameParent() throws Exception {
        testWithParentBothChildren();

        assertEquals(child1, child1.withParent(parent));
    }

    @Test
    public void testDelete() throws Exception {
        testWithParentBothChildren();
        child2.delete();

        MockImmutableParentChild newChild1, newParent;

        newParent = (MockImmutableParentChild) parent.getNext();
        assertNull(newParent.getNext());

        newChild1 = (MockImmutableParentChild) child1.getNext();
        assertNull(newChild1.getNext());

        assertEquals(Arrays.asList(newChild1), newParent.getChildren());
        assertNull(child2.getNext());
        assertFalse(newParent.getChildren().contains(child2));
    }


}

package BestSoFar.immutables;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
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
            list = (ImmutableListImpl<String>) list.makeReplacementFor(this);
        }

        @Override
        public boolean hasReplacement() {
            return false; // Dummy implementation
        }
    }

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
    public void testNotifyMutationHandler() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testGetList() throws Exception {

    }

    @Test
    public void testSize() throws Exception {

    }

    @Test
    public void testGetMutatedList() throws Exception {

    }
}

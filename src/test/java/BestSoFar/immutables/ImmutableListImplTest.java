package BestSoFar.immutables;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

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
    public void testMakeMutable() throws Exception {
        try {
            list.add("Won't work");
            throw new RuntimeException("Was mutable to begin with..");
        } catch (UnsupportedOperationException e) {}

        list.makeMutable();
        list.add("Hello");

        assertEquals(Arrays.asList("Hello"), list.getMutatedList());
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

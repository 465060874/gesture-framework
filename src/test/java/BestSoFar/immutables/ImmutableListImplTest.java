package BestSoFar.immutables;

import org.junit.Before;
import org.junit.Test;

/**
 * User: Sam Wright Date: 27/06/2013 Time: 11:32
 */
public class ImmutableListImplTest {
    class TestHandler implements ReplaceOnMutate<TestHandler> {

        @Override
        public void handleMutation() {
            throw new RuntimeException();
        }

        @Override
        public boolean hasReplacement() {
            return false; // Dummy implementation
        }
    }

    private TestHandler handler = new TestHandler();
    private ImmutableListImpl<String> list;

    @Before
    public void setUp() throws Exception {
        list = new ImmutableListImpl<>(handler);
    }

    @Test
    public void testMakeMutable() throws Exception {
        try {
            list.add("Won't work");
            throw new RuntimeException("Was mutable to begin with..");
        } catch (UnsupportedOperationException e) {}

        list.makeMutable();
        list.add("Hello");
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

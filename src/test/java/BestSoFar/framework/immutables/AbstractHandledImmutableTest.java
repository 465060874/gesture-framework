package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.EventuallyImmutable;
import BestSoFar.framework.immutables.common.ReplacementHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * User: Sam Wright Date: 08/07/2013 Time: 22:51
 */
public class AbstractHandledImmutableTest {
    public EventuallyImmutable original, replacement;

    public class MockHandledImmutable extends AbstractHandledImmutable {

        private int value;

        private boolean finalised = false;

        public MockHandledImmutable(boolean mutable, int value) {
            super(mutable);
            this.value = value;
        }

        public boolean isFinalised() {
            return finalised;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            if (isMutable())
                throw new UnsupportedOperationException();
            this.value = value;
        }

        @Override
        public EventuallyImmutable createClone(boolean mutable) {
            return new MockHandledImmutable(mutable, value);
        }

        @Override
        public void finalise() {
            finalised = true;
        }

        @Override
        public boolean isMutable() {
            return finalised;
        }
    }

    @Mock ReplacementHandler handler;
    public class MockHandler implements ReplacementHandler {


        @Override
        public void handleReplacement(EventuallyImmutable existingObject, EventuallyImmutable proposedObject) {
            original = existingObject;
            replacement = proposedObject;
        }
    }

    @Before
    public void setUp() throws Exception {
        Mock(handler)
    }

    @Test
    public void testAssignToHandler() throws Exception {

    }

    @Test
    public void testProposeReplacement() throws Exception {

    }
}

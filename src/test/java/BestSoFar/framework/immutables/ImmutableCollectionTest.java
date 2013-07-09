package BestSoFar.framework.immutables;

import BestSoFar.framework.immutables.common.EventuallyImmutable;
import BestSoFar.framework.immutables.common.ReplacementHandler;
import org.junit.Before;

import java.util.Collection;

/**
 * User: Sam Wright Date: 08/07/2013 Time: 22:42
 */
abstract public class ImmutableCollectionTest {

    abstract public Collection<String> createCollection();

    abstract public Collection<String> cloneCollection(Collection<String> collection);

    class MockHandler implements ReplacementHandler {
        private ImmutableWrapper handledCollection;

        public MockHandler() {
            handledCollection = new ImmutableList<>(false);
            finalise();
        }

        public Collection<String> getHandledCollection() {
            return (Collection<String>) handledCollection;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleReplacement(EventuallyImmutable existingObject, EventuallyImmutable proposedObject) {
            hasBeenNotified = true;
            handledCollection = (ImmutableList<String>) proposedObject;
        }

        public void finalise() {
            handledCollection.assignToHandler(this);
        }
    }

    @Before
    public void setUp() throws Exception {

    }
}

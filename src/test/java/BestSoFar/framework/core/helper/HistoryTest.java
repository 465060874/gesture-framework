package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * User: Sam Wright Date: 11/07/2013 Time: 13:13
 */
@RunWith(MockitoJUnitRunner.class)
public class HistoryTest {
    private History epoch, h1, h2, h3;

    @Mock
    private Processor p1, p2, p3;

    @Before
    public void setUp() throws Exception {
        epoch = History.getEpoch();

        h1 = epoch.createNext(p1);
        h2 = h1.createNext(p2);
        h3 = h2.createNext(p3);
    }

    @Test
    public void testCreateNextMultipleTimes() throws Exception {
        assertEquals(h1, epoch.createNext(p1));
        assertEquals(h2, h1.createNext(p2));
        assertEquals(h3, h2.createNext(p3));
    }

    @Test
    public void testCreateNextBranch() throws Exception {
        History h3b = h2.createNext(p1);

        assertEquals(p1, h3b.getCreator());
        assertEquals(h2, h3b.getPrevious());
        assertEquals(p3, h3.getCreator());
        assertEquals(h2, h3.getPrevious());

        assertEquals(h3b, h2.createNext(p1));
        assertEquals(h3, h2.createNext(p3));
    }

    @Test
    public void testGetPrevious() throws Exception {
        assertEquals(epoch, h1.getPrevious());
        assertEquals(h1, h2.getPrevious());
        assertEquals(h2, h3.getPrevious());
    }

    @Test
    public void testGetCreator() throws Exception {
        assertEquals(p1, h1.getCreator());
        assertEquals(p2, h2.getCreator());
        assertEquals(p3, h3.getCreator());
    }

    @Test
    public void testGetEpoch() throws Exception {
        assertEquals(epoch, History.getEpoch());
        assertEquals(null, epoch.getCreator());
        assertEquals(null, epoch.getPrevious());
    }

    @Test
    public void testDiscardFutureFrom() throws Exception {
        h2.discardFutureFrom(p3);
        assertNotSame(h3, h2.createNext(p3));
    }

    @Test
    public void testDiscardFutureFromWrongCreator() throws Exception {
        h2.discardFutureFrom(p1);
    }
}

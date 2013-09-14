package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.*;

/**
 * User: Sam Wright Date: 11/07/2013 Time: 13:13
 */
@RunWith(MockitoJUnitRunner.class)
public class HistoryTest {
    private History epoch, h1, h2a, h3a, h2b, h3b, h4;

    @Mock
    private Processor p1, p2a, p3a, p2b, p3b, p4;

    @Before
    public void setUp() throws Exception {
        epoch = History.getEpoch();
        h1 = epoch.createNext(p1);

        h2a = h1.createNext(p2a);
        h3a = h2a.createNext(p3a);

        h2b = h1.createNext(p2b);
        h3b = h2b.createNext(p3b);

        h4 = h1.join(p4, new HashSet<>(Arrays.asList(h3a, h3b)));
    }

    @Test
    public void testCreateNextMultipleTimes() throws Exception {
        assertEquals(h1, epoch.createNext(p1));
        assertEquals(h2a, h1.createNext(p2a));
        assertEquals(h3a, h2a.createNext(p3a));
    }

    @Test
    public void testCreateNextBranch() throws Exception {
        History h3b = h2a.createNext(p1);

        assertEquals(p1, h3b.getCreator());
        assertEquals(h2a, h3b.getPrevious());
        assertEquals(p3a, h3a.getCreator());
        assertEquals(h2a, h3a.getPrevious());

        assertEquals(h3b, h2a.createNext(p1));
        assertEquals(h3a, h2a.createNext(p3a));
    }

    @Test
    public void testGetPrevious() throws Exception {
        assertEquals(epoch, h1.getPrevious());
        assertEquals(h1, h2a.getPrevious());
        assertEquals(h2a, h3a.getPrevious());
    }

    @Test
    public void testGetCreator() throws Exception {
        assertEquals(p1, h1.getCreator());
        assertEquals(p2a, h2a.getCreator());
        assertEquals(p3a, h3a.getCreator());
    }

    @Test
    public void testGetEpoch() throws Exception {
        assertEquals(epoch, History.getEpoch());
        assertEquals(null, epoch.getCreator());
        assertEquals(null, epoch.getPrevious());
    }

    @Test
    public void testDiscardFutureFrom() throws Exception {
        h2a.discardFutureFrom(p3a);
        assertNotSame(h3a, h2a.createNext(p3a));
    }

    @Test
    public void testDiscardFutureFromWrongCreator() throws Exception {
        h2a.discardFutureFrom(p1);
    }

    @Test
    public void testJoin() throws Exception {
        assertTrue(h4.isJoinPoint());

        assertNotSame(h2a, h2b);
        assertNotSame(h3a, h3b);

        assertEquals(h4, h1.join(p4, new HashSet<>(Arrays.asList(h3a, h3b))));
    }

    @Test
    public void testGetJoined() throws Exception {
        assertEquals(new HashSet<>(Arrays.asList(h3a, h3b)), h4.getJoined());
    }

    @Test
    public void testGetAllCreators() throws Exception {
        Set<Processor> allCreators = new HashSet<>(Arrays.asList(p1, p2a, p2b, p3a, p3b, p4));
        assertEquals(allCreators, History.getAllCreators(h4));
    }
}

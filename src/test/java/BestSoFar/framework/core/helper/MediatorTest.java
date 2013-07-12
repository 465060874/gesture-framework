package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.Processor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Sam Wright Date: 11/07/2013 Time: 13:50
 */
@RunWith(MockitoJUnitRunner.class)
public class MediatorTest {

    @Mock
    private Processor<String, Integer> strToInt;
    @Mock
    private Processor<?, String> stringProvider;

    private Mediator<?> empty1, empty2;
    private Mediator<String> str1, str2;
    private Mediator<Integer> int1, int2;

    private String stringData = "5";
    private Integer intData = 5;

    @Before
    public void setUp() throws Exception {
        empty1 = Mediator.createEmpty();
        str1 = empty1.createNext(stringProvider, stringData);
        int1 = str1.createNext(strToInt, intData);

        empty2 = Mediator.createEmpty();
        str2 = empty2.createNext(stringProvider, stringData);
        int2 = str2.createNext(strToInt, intData);
    }

    @Test
    public void testCreateEmpty() throws Exception {
        assertNull(empty1.getData());
        assertNull(empty1.getPrevious());
        assertEquals(History.getEpoch(), empty1.getHistory());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(empty1.isEmpty());
        assertFalse(str1.isEmpty());
        assertFalse(int1.isEmpty());
    }

    @Test
    public void testGetData() throws Exception {
        assertEquals(stringData, str1.getData());
        assertEquals(intData, int1.getData());
    }

    @Test
    public void testGetHistory() throws Exception {
        assertEquals(stringProvider, str1.getHistory().getCreator());
        assertEquals(strToInt, int1.getHistory().getCreator());

        assertEquals(str1.getHistory(), int1.getHistory().getPrevious());
        assertEquals(History.getEpoch(), str1.getHistory().getPrevious());
    }

    @Test
    public void testGetPrevious() throws Exception {
        assertEquals(str1, int1.getPrevious());
        assertEquals(empty1, str1.getPrevious());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRollbackMediators() throws Exception {
        Set<Mediator<String>> rolledBack = Mediator.rollbackMediators(Sets.newSet(int1, int2));
        assertEquals(Sets.newSet(str1, str2), rolledBack);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRollbackSiblingMediators() throws Exception {
        int2 = str1.createNext(strToInt, intData);
        Set<Mediator<String>> rolledBack = Mediator.rollbackMediators(Sets.newSet(int1, int2));
        assertEquals(Sets.newSet(str1), rolledBack);
    }
}

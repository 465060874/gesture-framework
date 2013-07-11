package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.EventuallyImmutable;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 23:04
 */
@RunWith(MockitoJUnitRunner.class)
public class VersionInfoTest {
    private VersionInfo firstInfo, secondInfo, thirdInfo;

    @Mock
    private EventuallyImmutable first, second, third;

    @Before
    public void setUp() throws Exception {
        firstInfo = VersionInfo.createForFirst(first).withNext(second);
        secondInfo = VersionInfo.createForFirst(second).withPrevious(first).withNext(third);
        thirdInfo = VersionInfo.createForFirst(third).withPrevious(second);

        when(first.versionInfo()).thenReturn(firstInfo);
        when(second.versionInfo()).thenReturn(secondInfo);
        when(third.versionInfo()).thenReturn(thirdInfo);

        when(first.isDeleted()).thenReturn(false);
        when(second.isDeleted()).thenReturn(false);
        when(third.isDeleted()).thenReturn(false);
    }

    @Test
    public void testGetNext() throws Exception {
        Assert.assertEquals(firstInfo.getNext(), second);
        Assert.assertEquals(secondInfo.getNext(), third);
        assertNull(thirdInfo.getNext());
    }

    @Test
    public void testGetPrevious() throws Exception {
        assertNull(firstInfo.getPrevious());
        Assert.assertEquals(secondInfo.getPrevious(), first);
        Assert.assertEquals(thirdInfo.getPrevious(), second);
    }

    @Test
    public void testGetThisVersion() throws Exception {
        assertEquals(first, firstInfo.getThisVersion());
        assertEquals(second, secondInfo.getThisVersion());
        assertEquals(third, thirdInfo.getThisVersion());
    }

    @Test
    public void testWithNext() throws Exception {
        firstInfo = firstInfo.withNext(third);
        assertEquals(third, firstInfo.getNext());
        assertEquals(second, thirdInfo.getPrevious());
    }

    @Test
    public void testWithPrevious() throws Exception {
        thirdInfo = thirdInfo.withPrevious(first);
        assertEquals(first, thirdInfo.getPrevious());
        assertEquals(second, firstInfo.getNext());
    }

    @Test
    public void testGetLatest() throws Exception {
        assertEquals(third, firstInfo.getLatest());
    }

    @Test
    public void testGetEarliest() throws Exception {
        assertEquals(first, thirdInfo.getEarliest());
    }

    @Test
    public void testGetLatestWithNullNext() throws Exception {
        assertEquals(third, thirdInfo.getLatest());
    }

    @Test
    public void testGetEarliestWithNullPrevious() throws Exception {
        assertEquals(first, firstInfo.getEarliest());
    }

    @Test
    public void testCreateForFirst() throws Exception {
        firstInfo = VersionInfo.createForFirst(first);
        assertNull(firstInfo.getPrevious());
        assertNull(firstInfo.getNext());
        assertEquals(first, firstInfo.getThisVersion());
    }

    @Test
    public void testUpdateAllToLatest() throws Exception {
        List<EventuallyImmutable> immutables = new LinkedList<>(Arrays.asList(first, second, third));
        VersionInfo.updateAllToLatest(immutables);
        Assert.assertEquals(Arrays.asList(third, third, third), immutables);
    }

    @Test
    public void testUpdateAllToLatestWithDeleted() throws Exception {
        EventuallyImmutable deleted = mock(EventuallyImmutable.class);
        when(deleted.isDeleted()).thenReturn(true);
        when(deleted.versionInfo()).thenReturn(VersionInfo.createForFirst(deleted));

        List<EventuallyImmutable> immutables = new LinkedList<>(Arrays.asList(first, deleted));
        VersionInfo.updateAllToLatest(immutables);
        Assert.assertEquals(Arrays.asList(third), immutables);
    }
}

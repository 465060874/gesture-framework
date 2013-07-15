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

import static junit.framework.TestCase.assertEquals;

/**
 * User: Sam Wright Date: 15/07/2013 Time: 12:11
 */
@RunWith(MockitoJUnitRunner.class)
public class CompletedTrainingBatchTest {
    private CompletedTrainingBatch<Integer> batch;
    private Mediator<Integer> one, two, three;
    private Set<Mediator<Integer>> all, successful;

    @Mock
    private Processor<?,Integer> intProvider;

    @Before
    public void setUp() throws Exception {
        one = Mediator.createEmpty().createNext(intProvider, 1);
        two = Mediator.createEmpty().createNext(intProvider, 2);
        three = Mediator.createEmpty().createNext(intProvider, 3);
        all = new HashSet<>(Arrays.asList(one, two, three));
        successful = new HashSet<>(Arrays.asList(one, two));

        batch = new CompletedTrainingBatch<>(all, successful);
    }

    @Test
    public void testGetAll() throws Exception {
        assertEquals(all, batch.getAll());
    }

    @Test
    public void testGetSuccessful() throws Exception {
        assertEquals(successful, batch.getSuccessful());
    }

    @Test(expected = RuntimeException.class)
    public void testSuccessfulMustBeSubsetOfAll() throws Exception {
        batch = new CompletedTrainingBatch<>(successful, all);
    }
}

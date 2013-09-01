package io.github.samwright.framework.model;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.helper.ModelLoader;
import io.github.samwright.framework.model.helper.mock.MockProcessor;
import lombok.Getter;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.*;

/**
 * User: Sam Wright Date: 01/09/2013 Time: 00:03
 */
public class AbstractProcessorTest {
    private MockProcessor p1, p2, p3;
    private ModelController c1, c2;

    private class MockModelController implements ModelController {
        @Getter private Processor model, proposedModel;

        @Override
        public void proposeModel(Processor proposedModel) {
            this.proposedModel = proposedModel;
        }

        @Override
        public ModelController createClone() {
            return new MockModelController();
        }

        @Override
        public void handleUpdatedModel() {
            model = proposedModel;
            proposedModel = null;
        }
    }

    @Before
    public void setUp() throws Exception {
        p1 = new MockProcessor();
        p2 = new MockProcessor();
        p3 = new MockProcessor();

        c1 = new MockModelController();
        c2 = new MockModelController();

        p1.setController(c1);
    }

    private void setupLineage() {
        p2 = p1.createMutableClone();
        p2.replace(p1);
        p3 = p2.createMutableClone();
        p3.replace(p2);
    }

    @Test
    public void testGetUUID() throws Exception {
        UUID uuid = p1.getUUID();
        assertNotNull(uuid);
        assertEquals(p1, ModelLoader.getProcessor(uuid));
    }

    @Test
    public void testUUIDReplacement() throws Exception {
        UUID uuid = p1.getUUID();
        p2 = p1.createMutableClone();
        p2.replace(p1);
        assertEquals(uuid, p2.getUUID());
    }

    @Test
    public void testUUIDReplaceOld() throws Exception {
        UUID uuid1 = p1.getUUID();
        setupLineage();

        p2.discardPrevious();

        assertEquals(uuid1, p1.getUUID());
        UUID uuid2 = p2.getUUID();
        assertFalse(uuid1.equals(uuid2));
        assertEquals(uuid2, p3.getUUID());
        assertEquals(p1, p1.getCurrentVersion());
        assertEquals(p3, p3.getCurrentVersion());
        assertEquals(p3, p2.getCurrentVersion());
    }

    @Test
    public void testUUIDReplaceNew() throws Exception {
        UUID uuid1 = p1.getUUID();
        setupLineage();
        p1.setAsCurrentVersion();
        p1.discardNext();

        assertEquals(uuid1, p1.getUUID());
        UUID uuid2 = p2.getUUID();
        assertFalse(uuid1.equals(uuid2));
        assertEquals(p1, p1.getCurrentVersion());
    }

    @Test
    public void testSetAsCurrentVersion() throws Exception {
        setupLineage();
        p2.setAsCurrentVersion();
        assertEquals(p2, p1.getCurrentVersion());
        assertEquals(p2, p2.getCurrentVersion());
        assertEquals(p2, p3.getCurrentVersion());
    }

    @Test
    public void testGetController() throws Exception {
        setupLineage();
        assertEquals(c1, p1.getController());
        assertEquals(c1, p2.getController());
        assertEquals(c1, p3.getController());
    }

    @Test
    public void testDuplicateControllerWhenDiscarding() throws Exception {
        setupLineage();
        p1.discardNext();
        c2 = p2.getController();
        assertNotSame(c1, c2);
        assertEquals(c2, p3.getController());
    }

    @Test
    public void testMakeImmutableClone() throws Exception {
        p2 = p1.createMutableClone();
        p2.replace(p1);
        p2.discardPrevious();
        assertNotSame(p1.getController(), p2.getController());
        assertEquals(p1, p1.getCurrentVersion());
        assertEquals(p2, p2.getCurrentVersion());
    }
}

package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import org.junit.Test;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 20:17
 */
public class WorkflowImplTest {
    private static TypeData stringType = new TypeData(String.class, String.class);

    public static class SimpleContainer extends AbstractWorkflowContainer {

        public SimpleContainer() {
            super();
        }

        public SimpleContainer(AbstractWorkflowContainer oldWorkflowContainer) {
            super(oldWorkflowContainer);
        }

        @Override
        public AbstractWorkflowContainer createMutableClone() {
            return new SimpleContainer(this);
        }

        @Override
        public Mediator process(Mediator input) {
            return getChildren().get(0).process(input);
        }
    }

    @Test
    public void testSimple() throws Exception {

//        SimpleContainer container = new SimpleContainer();
//        Workflow<String, String> workflow = new WorkflowImpl<>(container, stringType);
//
//        container.getWorkflows().add(workflow);

    }

    @Test
    public void testNothing() throws Exception {
        System.out.println(" ==== HELLO WORLD!!! ====");

    }
}

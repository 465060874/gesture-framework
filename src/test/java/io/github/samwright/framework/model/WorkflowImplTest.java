package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import org.junit.Test;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 20:17
 */
public class WorkflowImplTest {
    private static TypeData<String,String> stringType = new TypeData<>(String.class, String.class);

    public static class SimpleContainer extends AbstractWorkflowContainer<String,String> {

        public SimpleContainer(TypeData<String, String> typeData) {
            super(typeData);
        }

        public SimpleContainer(AbstractWorkflowContainer<String, String> oldWorkflowContainer,
                               TypeData<String, String> typeData, boolean mutable) {
            super(oldWorkflowContainer, typeData);
        }

        @Override
        public AbstractWorkflowContainer<String, String> createMutableClone() {
            return new SimpleContainer(getTypeData());
        }

        @Override
        public Mediator<String> process(Mediator<?> input) {
            return getChildren().get(0).process(input);
        }

        @Override
        public <I2, O2> WorkflowContainer<I2, O2> withTypeData(TypeData<I2, O2> newTypeData) {
            throw new RuntimeException("not implemented");
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

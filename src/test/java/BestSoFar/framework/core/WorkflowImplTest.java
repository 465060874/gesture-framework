package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.core.helper.TypeData;
import org.junit.Test;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 20:17
 */
public class WorkflowImplTest {
    private static TypeData<String,String> stringType = new TypeData<>(String.class, String.class);

    public static class SimpleContainer extends AbstractWorkflowContainer<String,String> {


        public SimpleContainer(TypeData<String, String> typeData, boolean mutable) {
            super(typeData, mutable);
        }

        public SimpleContainer(AbstractWorkflowContainer<String, String> oldWorkflowContainer,
                               TypeData<String, String> typeData, boolean mutable) {
            super(oldWorkflowContainer, typeData, mutable);
        }

        @Override
        public AbstractWorkflowContainer<String, String> createMutableClone(boolean mutable) {
            return new SimpleContainer(getTypeData(), mutable);
        }

        @Override
        public Mediator<String> process(Mediator<?> input) {
            return getWorkflows().get(0).process(input);
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

package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.Mediator;
import BestSoFar.framework.core.helper.TypeData;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

package BestSoFar.immutables;

import BestSoFar.framework.core.AbstractWorkflowContainer;
import BestSoFar.framework.core.Processor;
import BestSoFar.framework.core.Workflow;
import BestSoFar.framework.core.WorkflowImpl;
import BestSoFar.framework.helper.Mediator;

/**
 * User: Sam Wright Date: 27/06/2013 Time: 16:05
 */
public class ImmutableListPSVM {
    private static TypeData<String, String> stringType = new TypeData<>(String.class, String.class);

    private static class SimpleContainer extends AbstractWorkflowContainer<String, String> {

        public SimpleContainer() {
            super((Workflow<?, ?>) null, stringType);
        }

        public SimpleContainer(AbstractWorkflowContainer<String, String> oldWorkflowContainer, TypeData<String, String> typeData) {
            super(oldWorkflowContainer, typeData);
        }

        @Override
        public Mediator<String> process(Mediator<?> input) {
            return getWorkflows().get(0).process(input);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I2, O2> Processor<I2, O2> cloneAs(TypeData<I2, O2> typeData) {
            return (Processor<I2, O2>) new SimpleContainer(this, getTypeData());
        }
    }


    public static void main(String... args) {

        SimpleContainer container = new SimpleContainer();
        Workflow<String, String> workflow = new WorkflowImpl<>(container, stringType);

        container.getWorkflows().add(workflow);

    }
}

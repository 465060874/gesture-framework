package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Sam Wright Date: 08/09/2013 Time: 15:34
 */
public abstract class ChooserWorkflowContainer extends AbstractWorkflowContainer {

    protected ChooserWorkflowContainer(TypeData typeData) {
        super(typeData);
    }

    protected ChooserWorkflowContainer(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Mediator process(Mediator input) {
        return chooseWorkflow(input).process(input);
    }

    public abstract Workflow chooseWorkflow(Mediator input);

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        List<Mediator> outputs = new ArrayList<>();

        for (Workflow workflow : getChildren())
            outputs.add(workflow.process(input));

        return outputs;
    }

    @Override
    public abstract ChooserWorkflowContainer createMutableClone();

    public abstract void handleSuccessfulWorkflows(
            Map<Mediator, List<Workflow>> successfulWorkflowsByInput);

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        Map<Mediator, List<Workflow>> successfulWorkflowsByInput = new HashMap<>();

        for (Mediator input : Mediator.rollbackMediators(completedTrainingBatch.getAll())) {
            successfulWorkflowsByInput.put(input, new ArrayList<Workflow>());
        }

        for (Mediator successfulOutput : completedTrainingBatch.getSuccessful()) {
            List<Workflow> successfulWorkflows
                    = successfulWorkflowsByInput.get(successfulOutput.getPrevious());

            Workflow successfulWorkflow = (Workflow) successfulOutput.getHistory().getCreator();
            successfulWorkflows.add(successfulWorkflow);
        }

        handleSuccessfulWorkflows(successfulWorkflowsByInput);

        return super.processCompletedTrainingBatch(completedTrainingBatch);
    }
}

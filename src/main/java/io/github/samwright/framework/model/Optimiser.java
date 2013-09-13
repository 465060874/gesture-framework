package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright Date: 13/09/2013 Time: 08:16
 */
public class Optimiser extends ChooserWorkflowContainer {

    private Workflow chosenWorkflow;

    public Optimiser() {
        super(TypeData.getDefaultType());
    }

    public Optimiser(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Workflow chooseWorkflow(Mediator input) {
        return chosenWorkflow;
    }

    @Override
    public ChooserWorkflowContainer createMutableClone() {
        return new Optimiser(this);
    }

    @Override
    public void handleSuccessfulInputBatches(Map<Workflow, CompletedTrainingBatch> inputBatchesByWorkflow) {

        double maxSuccessRate = -1.;

        for (Map.Entry<Workflow, CompletedTrainingBatch> e : inputBatchesByWorkflow.entrySet()) {
            Workflow workflow = e.getKey();
            Set<Mediator> allInputs = e.getValue().getAll();
            Set<Mediator> successfulInputs = e.getValue().getSuccessful();

            double successRate = successfulInputs.size() * 1. / allInputs.size();
            if (successRate > maxSuccessRate) {
                maxSuccessRate = successRate;
                chosenWorkflow = workflow;
            }
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid() && chosenWorkflow != null;
    }
}

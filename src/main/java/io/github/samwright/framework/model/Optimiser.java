package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright Date: 13/09/2013 Time: 08:16
 */
public class Optimiser extends ChooserWorkflowContainer {

    private Workflow chosenWorkflow;
    @Getter private Map<Workflow,Double> successRates = new HashMap<>();

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
        successRates.clear();

        for (Map.Entry<Workflow, CompletedTrainingBatch> e : inputBatchesByWorkflow.entrySet()) {
            Workflow workflow = e.getKey();
            Set<Mediator> allInputs = e.getValue().getAll();
            Set<Mediator> successfulInputs = e.getValue().getSuccessful();

            double successRate;
            if (allInputs.size() == 0)
                successRate = 0;
            else
                successRate = successfulInputs.size() * 1. / allInputs.size();

            successRates.put(workflow, successRate);

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

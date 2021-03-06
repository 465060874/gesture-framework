package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.History;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ChooserWorkflowContainer} that statically decides during training which
 * {@link Workflow} is best suited to each {@link History} object of its incoming
 * {@link Mediator} objects.
 */
public class Optimiser extends ChooserWorkflowContainer {

    private Map<History,Workflow> chosenWorkflows = new HashMap<>();
    @Getter private Map<Workflow,Map<History,Double>> successRates = new HashMap<>();

    public Optimiser() {
        super(TypeData.getDefaultType());
    }

    public Optimiser(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Workflow chooseWorkflow(Mediator input) {
        return chosenWorkflows.get(input.getHistory());
    }

    @Override
    public ChooserWorkflowContainer createMutableClone() {
        return new Optimiser(this);
    }

    @Override
    public void handleSuccessfulInputsByWorkflowAndHistory(
            Map<History, Set<Mediator>> allInputsByHistory,
            Map<History, Map<Workflow, Set<Mediator>>> successfulInputsByWorkflowAndHistory) {

        successRates.clear();
        chosenWorkflows.clear();

        for (Map.Entry<History, Map<Workflow, Set<Mediator>>> e1 : successfulInputsByWorkflowAndHistory.entrySet()) {
            History history = e1.getKey();
            Set<Mediator> allInputsForHistory = allInputsByHistory.get(history);

            double maxSuccessRateForHistory = -1.;
            Workflow bestWorkflowForHistory = null;

            for (Map.Entry<Workflow, Set<Mediator>> e2 : e1.getValue().entrySet()) {
                Workflow workflow = e2.getKey();
                Set<Mediator> successfulInputsForWorkflowAndHistory = e2.getValue();

                Map<History, Double> workflowSuccessRateByHistory = successRates.get(workflow);

                if (workflowSuccessRateByHistory == null) {
                    workflowSuccessRateByHistory = new HashMap<>();
                    successRates.put(workflow, workflowSuccessRateByHistory);
                }

                double successRate = successfulInputsForWorkflowAndHistory.size()
                                        * 1. / allInputsForHistory.size();

                workflowSuccessRateByHistory.put(history, successRate);

                if (successRate > maxSuccessRateForHistory) {
                    maxSuccessRateForHistory = successRate;
                    bestWorkflowForHistory = workflow;
                }
            }
            chosenWorkflows.put(history, bestWorkflowForHistory);
        }


    }

    @Override
    public boolean isValid() {
        return super.isValid() && !chosenWorkflows.isEmpty();
    }
}

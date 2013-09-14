package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Optimiser;
import io.github.samwright.framework.model.Workflow;

/**
 * User: Sam Wright Date: 13/09/2013 Time: 09:43
 */
public class OptimiserController extends WorkflowContainerControllerImpl {

    {
        containerLabel.setText("Optimiser");
    }

    public OptimiserController() {
        super();
        Optimiser model = new Optimiser();
        proposeModel(model);
        setElementLink(new ElementLink());
        addNewWorkflow();
    }

    public OptimiserController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public Optimiser getModel() {
        return (Optimiser) super.getModel();
    }

    @Override
    public WorkflowContainerController createClone() {
        return new OptimiserController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        for (int i = 0; i < getModel().getChildren().size(); ++i) {
            Workflow workflow = getModel().getChildren().get(i);
            Double successRate = getModel().getSuccessRates().get(workflow);

            if (successRate == null)
                relabelWorkflow(i, "Not trained");
            else
                relabelWorkflow(i, String.format("%.1f%% success rate", successRate * 100));
        }
    }
}

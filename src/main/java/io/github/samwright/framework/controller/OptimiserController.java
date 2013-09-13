package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Optimiser;

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
    public WorkflowContainerController createClone() {
        return new OptimiserController(this);
    }
}

package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.controller.WorkflowContainerController;
import io.github.samwright.framework.controller.WorkflowContainerControllerImpl;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 24/08/2013 Time: 09:19
 */
public class ExContainerController extends WorkflowContainerControllerImpl {
    public ExContainerController() {
        super();
        setModel(new ExContainer());
        setElementLink(new ElementLink());
        handleUpdatedModel();
        addNewWorkflow();
    }

    public ExContainerController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public WorkflowContainerController createClone() {
        return new ExContainerController(this);
    }
}

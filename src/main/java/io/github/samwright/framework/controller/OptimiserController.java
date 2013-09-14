package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.javacv.helper.HistoryHighlighter;
import io.github.samwright.framework.model.Optimiser;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.helper.History;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        List<WorkflowControllerImpl> workflowControllers = new LinkedList<>();
        for (Node child : workflowsBox.getChildren()) {
            if (child instanceof WorkflowController)
                workflowControllers.add((WorkflowControllerImpl) child);
        }

        for (WorkflowControllerImpl workflowController : workflowControllers) {
            workflowController.getHeader().getChildren().clear();
            System.out.println("Header should have 0 children: " + workflowController.getHeader()
                    .getChildren());

            Workflow workflow = workflowController.getModel();
            Map<History,Double> successRateByHistory = getModel().getSuccessRates().get(workflow);

            // If workflow hasn't been trained, skip it.
            if (successRateByHistory == null)
                continue;

            addConfigNode(workflowController.getHeader());

            for (Map.Entry<History,Double> e : successRateByHistory.entrySet()) {
                History history = e.getKey();
                double successRate = e.getValue();

                String label = String.format("%.1f%%", successRate * 100);
                Button button = new HistoryHighlighter(history, this, label);

                workflowController.getHeader().getChildren().add(button);
            }
            System.out.println("Header has children: " + workflowController.getHeader()
                    .getChildren());
        }
    }
}

package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.History;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.*;

/**
 * User: Sam Wright Date: 08/09/2013 Time: 15:34
 */
public abstract class ChooserWorkflowContainer extends AbstractWorkflowContainer {

    public ChooserWorkflowContainer(TypeData typeData) {
        super(typeData);
    }

    public ChooserWorkflowContainer(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
    }

    @Override
    public Mediator process(Mediator input) {
        Mediator output = chooseWorkflow(input).process(input);
        return output.createNext(this, output.getData());
    }

    public abstract Workflow chooseWorkflow(Mediator input);

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        List<Mediator> outputs = new ArrayList<>(), finalOutputs = new ArrayList<>();

        for (Workflow workflow : getChildren())
            outputs.add(workflow.process(input));

        for (Mediator output : outputs)
            finalOutputs.add(output.createNext(this, output.getData()));

        return finalOutputs;
    }

    @Override
    public abstract ChooserWorkflowContainer createMutableClone();

    /**
     * Decide on a workflow-choosing strategy (ie. so {@code chooseWorkflow(Mediator m)} gives a
     * sensible answer).
     *
     * @param allInputsByHistory all training data given to this object, indexed by their History
     *                           objects.
     * @param successfulInputsByWorkflowAndHistory the training data given to this object that
     *                                             went on to be successfully classified,
     *                                             indexed by the training data's history,
     *                                             and the workflow that successfully processed it.
     */
    public abstract void handleSuccessfulInputsByWorkflowAndHistory(
            Map<History, Set<Mediator>> allInputsByHistory,
            Map<History, Map<Workflow, Set<Mediator>>> successfulInputsByWorkflowAndHistory);

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        CompletedTrainingBatch batch = super.processCompletedTrainingBatch(completedTrainingBatch);

        Map<Workflow, Set<Mediator>> successfulOutputsByWorkflow = new HashMap<>();
        Map<Workflow, Set<Mediator>> allOutputsByWorkflow = new HashMap<>();

        // Separate output mediators by workflow

        for (Workflow workflow : getChildren()) {
            successfulOutputsByWorkflow.put(workflow, new HashSet<Mediator>());
            allOutputsByWorkflow.put(workflow, new HashSet<Mediator>());
        }

        for (Mediator successfulOutput : batch.getSuccessful()) {
            Workflow creator = (Workflow) successfulOutput.getHistory().getCreator();
            successfulOutputsByWorkflow.get(creator).add(successfulOutput);
        }

        for (Mediator output : batch.getAll()) {
            Workflow creator = (Workflow) output.getHistory().getCreator();
            allOutputsByWorkflow.get(creator).add(output);
        }

        // Let workflows process their respective batches:

        Map<Workflow, CompletedTrainingBatch> inputBatchesByWorkflow = new HashMap<>();
        for (Workflow workflow : getChildren()) {
            CompletedTrainingBatch workflowOutputBatch = new CompletedTrainingBatch(
                    allOutputsByWorkflow.get(workflow),
                    successfulOutputsByWorkflow.get(workflow)
            );

            CompletedTrainingBatch workflowInputBatch
                    = workflow.processCompletedTrainingBatch(workflowOutputBatch);

            inputBatchesByWorkflow.put(workflow, workflowInputBatch);
        }

        Set<Mediator> allInputs = null;
        Map<History, Map<Workflow, Set<Mediator>>> successfulInputsByWorkflowAndHistory
                = new HashMap<>();

        // Process the batches returned by the child workflows (ie. containing mediators that
        // were this object's inputs).
        for (Map.Entry<Workflow, CompletedTrainingBatch> e : inputBatchesByWorkflow.entrySet()) {
            Workflow workflow = e.getKey();
            CompletedTrainingBatch inputBatch = e.getValue();
            Set<Mediator> successfulInputsForWorkflow = inputBatch.getSuccessful();

            // Make sure all input batches now share the same 'getAll()':
            if (allInputs == null)
                allInputs = inputBatch.getAll();
            else if (!allInputs.equals(inputBatch.getAll()))
                throw new RuntimeException("Child workflows did not properly rollback");

            // Index the input data that goes on to be successful by History and Workfow
            for (Mediator successfulInputForWorkflow : successfulInputsForWorkflow) {
                History history = successfulInputForWorkflow.getHistory();

                Map<Workflow, Set<Mediator>> successfulInputsForHistoryByWorkflow
                        = successfulInputsByWorkflowAndHistory.get(history);

                // If this is a new history object ...
                if (successfulInputsForHistoryByWorkflow == null) {
                    // ... create a new map of workflows to successful mediators they processed ...
                    successfulInputsForHistoryByWorkflow = new HashMap<>();
                    // ... and assign an empty list for each workflow
                    for (Workflow childWorkflow : getChildren())
                        successfulInputsForHistoryByWorkflow.put(childWorkflow, new HashSet<Mediator>());

                    successfulInputsByWorkflowAndHistory.put(history, successfulInputsForHistoryByWorkflow);
                }

                // Get the set of mediators that this workflow successfully processed (which has
                // already been initialised as an empty HashSet).
                Set<Mediator> successfulInputsForWorkflowAndHistory
                        = successfulInputsForHistoryByWorkflow.get(workflow);

                // Finally, add the successful input to the set for this workflow and history.
                successfulInputsForWorkflowAndHistory.add(successfulInputForWorkflow);
            }
        }

        if (allInputs == null)
            allInputs = Collections.emptySet();

        // Index allInputs by their History objects
        Map<History,Set<Mediator>> allInputsByHistory = new HashMap<>();
        for (Mediator mediator : allInputs) {
            History history = mediator.getHistory();
            Set<Mediator> allInputsForHistory = allInputsByHistory.get(history);

            if (allInputsForHistory == null) {
                allInputsForHistory = new HashSet<>();
                allInputsByHistory.put(history, allInputsForHistory);
            }

            allInputsForHistory.add(mediator);
        }

        // Let the concrete implementer use the completed training batch from the inputs of the
        // workflows to decide how to choose the correct workflow in future.
        handleSuccessfulInputsByWorkflowAndHistory(allInputsByHistory, successfulInputsByWorkflowAndHistory);

        // Find all input mediators that would have been chosen, if they now went to
        // 'process(input)'
        Set<Mediator> successfulChosenInputs = new HashSet<>();
        Set<Mediator> allChosenInputs = new HashSet<>();
        for (Workflow workflow : getChildren()) {
            Set<Mediator> successfulWorkflowInputs
                    = inputBatchesByWorkflow.get(workflow).getSuccessful();
            Set<Mediator> allWorkflowInputs = inputBatchesByWorkflow.get(workflow).getAll();

            for (Mediator input : allWorkflowInputs)
                if (chooseWorkflow(input) == workflow)
                    allChosenInputs.add(input);

            for (Mediator successfulInput : successfulWorkflowInputs)
                if (chooseWorkflow(successfulInput) == workflow)
                    successfulChosenInputs.add(successfulInput);
        }

        return new CompletedTrainingBatch(allChosenInputs, successfulChosenInputs);
    }

}

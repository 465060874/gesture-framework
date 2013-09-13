package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
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
     * sensible answer) given the {@link CompletedTrainingBatch} which contains all of the
     * training data mediators which were passed to {@code processTrainingData(..)} (ie. for each
     * batch in the map, batch.getAll() gives the same set).
     * <p/>
     * However each batch has been sent back along the workflow that created it,
     * so each batch's 'successful' set contains the input mediators which were processed by
     * the relevant workflow and went on to be successfully classified.
     *
     * @param inputBatchesByWorkflow the batches returned when each workflow processes the
     *                               completed training batch passed to this object.
     */
    public abstract void handleSuccessfulInputBatches(Map<Workflow, CompletedTrainingBatch> inputBatchesByWorkflow);

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

        // Let the concrete implementer use the completed training batch from the inputs of the
        // workflows to decide how to choose the correct workflow in future.
        handleSuccessfulInputBatches(inputBatchesByWorkflow);

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

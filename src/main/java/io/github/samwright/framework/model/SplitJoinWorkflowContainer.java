package io.github.samwright.framework.model;

import com.google.common.collect.Sets;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.*;

/**
 * Run best 1, output 1.
 * Run all, output collection of all.
 *
 * User: Sam Wright Date: 07/09/2013 Time: 10:46
 */
public abstract class SplitJoinWorkflowContainer extends AbstractWorkflowContainer {

    @Getter private List<TypeData> requiredWorkflowTypeData;

    public SplitJoinWorkflowContainer(TypeData typeData, List<TypeData> requiredWorkflowTypeData) {
        super(typeData);
        this.requiredWorkflowTypeData = requiredWorkflowTypeData;
    }

    public SplitJoinWorkflowContainer(SplitJoinWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
        this.requiredWorkflowTypeData = oldWorkflowContainer.getRequiredWorkflowTypeData();
    }

    public SplitJoinWorkflowContainer withRequiredWorkflowTypeData(List<TypeData> requiredWorkflowTypeData) {
        if (isMutable()) {
            this.requiredWorkflowTypeData = requiredWorkflowTypeData;
            return this;
        } else {
            return createMutableClone().withRequiredWorkflowTypeData(requiredWorkflowTypeData);
        }
    }

    @Override
    public abstract SplitJoinWorkflowContainer createMutableClone();

    @Override
    public boolean isValid() {
        if (requiredWorkflowTypeData.size() != getChildren().size())
            return false;

        for (int i = 0; i < requiredWorkflowTypeData.size(); ++i) {
            Workflow workflow = getChildren().get(i);
            if (!workflow.getTypeData().equals(requiredWorkflowTypeData.get(i)))
                return false;
        }

        return super.isValid();
    }

    @Override
    public Mediator process(Mediator input) {
        final ArrayList<Mediator> outputs = new ArrayList<>();

        for (Workflow workflow : getChildren()) {
            Mediator preparedInput = input.createNext(workflow, input.getData());
            processWorkflow(workflow, preparedInput, outputs);
        }

        synchronized (outputs) {
            while (outputs.size() != getChildren().size()) {
                try {
                    outputs.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Order by workflow:
        ArrayList<Mediator> orderedOutputs = new ArrayList<>();
        for (int i = 0; i < getChildren().size(); ++i) {
            orderedOutputs.add(null);
        }

        for (Mediator output : outputs) {
            Workflow creator = (Workflow) output.getHistory().getCreator();
            int index = getChildren().indexOf(creator);
            orderedOutputs.set(index, output);
        }

        return input
                .createNext(this, orderedOutputs)
                .createNext(this, joinOutputMediators(orderedOutputs));
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        List<Set<Mediator>> allWorkflowsOutputs = new ArrayList<>();

        for (Workflow workflow : getChildren())
            allWorkflowsOutputs.add(new HashSet<>(workflow.processTrainingData(input)));

        Set<List<Mediator>> combinations = Sets.cartesianProduct(allWorkflowsOutputs);

        List<Mediator> outputs = new ArrayList<>();
        for (List<Mediator> combination : combinations) {
            outputs.add(input
                    .createNext(this, combination)
                    .createNext(this, joinOutputMediators(combination)));
        }

        return outputs;
    }

    public abstract Object joinOutputMediators(List<Mediator> mediators);

    private void processWorkflow(final Workflow workflow,
                                 final Mediator mediator,
                                 final List<Mediator> outputs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mediator output = null;
                try {
                    output = workflow.process(mediator);
                } finally {
                    synchronized (outputs) {
                        outputs.add(output);
                        outputs.notifyAll();
                    }
                }

            }
        }).start();
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        // Rollback past the data mediator as normal...
        completedTrainingBatch = super.processCompletedTrainingBatch(completedTrainingBatch);

        // Now each mediator's data is the list of mediators that joined to create it.
        Map<Workflow, Set<Mediator>> allMediatorsMap
                = rollbackMediatorsToWorkflows(completedTrainingBatch.getAll());
        Map<Workflow, Set<Mediator>> successfulMediatorsMap
                = rollbackMediatorsToWorkflows(completedTrainingBatch.getSuccessful());

        // Let workflows process the completed set...
        for (Map.Entry<Workflow, Set<Mediator>> entry : allMediatorsMap.entrySet()) {
            Workflow workflow = entry.getKey();
            Set<Mediator> allMediators = allMediatorsMap.get(workflow);
            Set<Mediator> successfulMediators = successfulMediatorsMap.get(workflow);

            if (successfulMediators == null)
                successfulMediators = Collections.emptySet();

            CompletedTrainingBatch workflowBatch
                    = new CompletedTrainingBatch(allMediators, successfulMediators);

            workflow.processCompletedTrainingBatch(workflowBatch);
        }

        return completedTrainingBatch.rollBack();
    }

    @SuppressWarnings("unchecked")
    private Map<Workflow, Set<Mediator>> rollbackMediatorsToWorkflows(Set<Mediator> mediators) {
        Map<Workflow, Set<Mediator>> mediatorsPerWorkflow = new HashMap<>();

        for (Mediator mediator : mediators) {
            List<Mediator> outputsBeforeJoin = (List<Mediator>) mediator.getData();
            for (Mediator outputBeforeJoin : outputsBeforeJoin) {
                Workflow creator = (Workflow) outputBeforeJoin.getHistory().getCreator();
                Set<Mediator> mediatorsFromCreator = mediatorsPerWorkflow.get(creator);

                if (mediatorsFromCreator == null) {
                    mediatorsFromCreator = new HashSet<>();
                    mediatorsPerWorkflow.put(creator, mediatorsFromCreator);
                }

                mediatorsFromCreator.add(outputBeforeJoin);
            }
        }

        return mediatorsPerWorkflow;
    }
}

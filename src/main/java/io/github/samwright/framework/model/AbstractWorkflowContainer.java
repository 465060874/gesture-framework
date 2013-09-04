package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.Replaceable;
import io.github.samwright.framework.model.datatypes.Helper;
import io.github.samwright.framework.model.helper.ChildrenManager;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import org.w3c.dom.Document;

import java.util.*;

/**
 * Abstract implementation of {@link WorkflowContainer}.
 * <p/>
 * Concrete {@code WorkflowContainer} implementations can derive from this to let it handle the
 * list of {@link Workflow} objects in this, along with everything handled in
 * {@link AbstractElement}.
 */
public abstract class AbstractWorkflowContainer
        extends AbstractElement implements WorkflowContainer {

    private final ChildrenManager<Workflow, WorkflowContainer> childrenManager;
//    @Getter private TypeData requiredTypeData;
    private TypeData downconvertedTypeData;
    private boolean outputTypeFixed = false;
    private Object[] typeDataLock = new Object[0];

    /**
     * Constructs the initial (and immutable) {@code AbstractWorkflowContainer}.
     */
    public AbstractWorkflowContainer() {
        this(TypeData.getDefaultType());
    }

    public AbstractWorkflowContainer(TypeData typeData) {
        super(typeData);
        childrenManager = new ChildrenManager<Workflow, WorkflowContainer>(this);
//        requiredTypeData = typeData;
        resetDerivedTypeData();
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractWorkflowContainer}.
     *
     * @param oldWorkflowContainer the {@code AbstractWorkflowContainer} to clone.
     */
    public AbstractWorkflowContainer(AbstractWorkflowContainer oldWorkflowContainer) {
        super(oldWorkflowContainer);
        childrenManager =
                new ChildrenManager<Workflow, WorkflowContainer>
                (this, oldWorkflowContainer.getChildren());
        resetDerivedTypeData();
    }

    @Override
    public boolean isValid() {
        if (getChildren().isEmpty())
            return false;

        return true;
    }

    public TypeData getRequiredTypeData() {
        return super.getTypeData();
    }

    @Override
    public WorkflowContainer withParent(Workflow newParent) {
        return (WorkflowContainer) super.withParent(newParent);
    }

    @Override
    public boolean areChildrenValid() {
        return childrenManager.areChildrenValid();
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        List<Mediator> outputs = new ArrayList<>();

        for (Workflow workflow : getChildren())
            outputs.addAll(workflow.processTrainingBatch(inputs));

        return outputs;
    }

    @Override
    public void discardNext() {
        super.discardNext();
        childrenManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        super.discardPrevious();
        childrenManager.discardPrevious();
    }

    @Override
    public void setAsCurrentVersion() {
        if (this != getCurrentVersion()) {
            super.setAsCurrentVersion();
            if (childrenManager != null)
                childrenManager.setAsCurrentVersion();
        }
    }

    @Override
    public void replace(Replaceable toReplace) {
        setReplacing(true);
        childrenManager.beforeReplacing((WorkflowContainer) toReplace);
        super.replace(toReplace);
    }

    @Override
    public List<Workflow> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    public WorkflowContainer withChildren(List<Workflow> newChildren) {
        return childrenManager.withChildren(newChildren);
    }

    @Override
    public WorkflowContainer withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        WorkflowContainer clone = (WorkflowContainer) super.withXML(node, map);
        if (clone != this)
            return clone;

        childrenManager.withXML(node, map);
        return this;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        node.appendChild(childrenManager.getXMLForDocument(doc));
        return node;
    }

    @Override
    public WorkflowContainer getCurrentVersion() {
        return (WorkflowContainer) super.getCurrentVersion();
    }

    @Override
    public String getXMLTag() {
        return "WorkflowContainer";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterReplacement() {
        childrenManager.afterReplacement();

        if (getParent() == null)
            return;

        synchronized (typeDataLock) {
            int myIndex = getParent().getChildren().indexOf(this);
            Class inputType;

            if (myIndex == 0)
                // Input data types are calculated here in a breadth-first search,
                // meaning the parent's TypeData is already calculated.
                inputType = getParent().getTypeData().getInputType();
            else
                // The previous sibling might be another WorkflowContainer,
                // containing other WorkflowContainers that haven't had their TypeData calculated yet.
                // However, we're only looking at their output type, which is lazily initialised (and
                // never dependent on children input types).
                inputType = getParent().getChildren().get(myIndex - 1).getTypeData().getOutputType();

            Class requiredInput = super.getTypeData().getInputType();

            if (!requiredInput.isAssignableFrom(inputType))
                inputType = requiredInput;

            downconvertedTypeData = new TypeData(inputType, downconvertedTypeData.getOutputType());
        }
    }

    @Override
    public TypeData getTypeData() {
        synchronized (typeDataLock) {
            if (outputTypeFixed)
                return downconvertedTypeData;

            List<Class> workflowOutputs = new LinkedList<>();
            for (Workflow workflow : getChildren()) {
                if (workflow.getChildren().isEmpty()) {
                    workflowOutputs.add(downconvertedTypeData.getInputType());
                } else {
                    int lastElementIndex = workflow.getChildren().size() - 1;
                    Element lastElement = workflow.getChildren().get(lastElementIndex);
                    workflowOutputs.add(lastElement.getTypeData().getOutputType());
                }
            }


            Class<?> outputType, requiredOutput = super.getTypeData().getOutputType();

            if (workflowOutputs.isEmpty())
                outputType = requiredOutput;
            else {
                outputType = Helper.lowestCommonAncestor(workflowOutputs);
                if (!requiredOutput.isAssignableFrom(outputType))
                    outputType = requiredOutput;
            }

            downconvertedTypeData = new TypeData(downconvertedTypeData.getInputType(), outputType);
            outputTypeFixed = true;

            return downconvertedTypeData;
        }
    }

    private void resetDerivedTypeData() {
        downconvertedTypeData = super.getTypeData();
        outputTypeFixed = false;
    }

    @Override
    public WorkflowContainer withTypeData(TypeData typeData) {
        if (isMutable()) {
            synchronized (typeDataLock) {
                super.withTypeData(typeData);
                resetDerivedTypeData();
            }
            return this;
        } else {
            return createMutableClone().withTypeData(typeData);
        }
    }
}

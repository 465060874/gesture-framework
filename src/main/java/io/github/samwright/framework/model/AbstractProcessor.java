package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.ModelLoader;
import io.github.samwright.framework.model.helper.MutabilityHelper;
import lombok.AccessLevel;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract implementation of {@link Processor}.
 */
public abstract class AbstractProcessor implements Processor {

    @Delegate private final MutabilityHelper mutabilityHelper;
    @Setter(AccessLevel.PROTECTED) @Getter private boolean replacing = false;


    public AbstractProcessor() {
        this.mutabilityHelper = new MutabilityHelper(this, false);
        setUUID(ModelLoader.makeNewUUID());
    }

    public AbstractProcessor(AbstractProcessor oldProcessor) {
        if (oldProcessor.isMutable())
            throw new RuntimeException("Cannot clone a mutable processor");
        this.mutabilityHelper = new MutabilityHelper(this, true);
        setUUID(ModelLoader.makeNewUUID());
    }

    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        String processorString = getXMLTag();
        if (processorString == null)
            processorString = "Processor";

        org.w3c.dom.Element element = doc.createElement(processorString);
        element.setAttribute("model", getModelIdentifier());
        element.setAttribute("UUID", getUUID().toString());

        return element;
    }

    public Processor withXML(org.w3c.dom.Element node, Map<UUID, Processor> dictionary) {
        if (!isMutable())
            return createMutableClone().withXML(node, dictionary);

        String uuidString = node.getAttribute("UUID");
        dictionary.put(UUID.fromString(uuidString), this);
        return this;
    }

    @Override
    public String getModelIdentifier() {
        return getClass().getName();
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        // Check typedata
        if (completedTrainingBatch.getAll().isEmpty())
            return completedTrainingBatch;

        Mediator first = completedTrainingBatch.getAll().iterator().next();
        if (first.getData() != null) {
            Processor creator = first.getHistory().getCreator();
            if (creator != this) {
                throw new RuntimeException("Training batch was created by: " + creator
                                        + " instead of this: " + this);
            }

            Class<?> myOutput = getTypeData().getOutputType();
            Class<?> outputDataType = first.getData().getClass();
            if (!myOutput.isAssignableFrom(outputDataType))
                throw new RuntimeException("Training batch had output type: " + outputDataType
                                         + " but this processor only outputs type: " + myOutput);
        }

        return completedTrainingBatch.rollBack();
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        return Arrays.asList(process(input));
    }

    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }
}
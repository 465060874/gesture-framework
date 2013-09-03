package io.github.samwright.framework.model;

import io.github.samwright.framework.model.helper.ModelLoader;
import io.github.samwright.framework.model.helper.MutabilityHelper;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.AccessLevel;
import lombok.Delegate;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 31/08/2013 Time: 12:53
 */
public abstract class AbstractProcessor implements Processor {

    @Delegate private final MutabilityHelper mutabilityHelper;
    @Getter private TypeData typeData;
    @Setter(AccessLevel.PROTECTED) @Getter private boolean replacing = false;


    public AbstractProcessor() {
        this.mutabilityHelper = new MutabilityHelper(this, false);
        this.typeData = TypeData.getDefaultType();
        setUUID(ModelLoader.makeNewUUID());
//        this.uuid = ModelLoader.makeNewUUID();
//        ModelLoader.registerProcessor(this);
    }

    public AbstractProcessor(AbstractProcessor oldProcessor) {
        if (oldProcessor.isMutable())
            throw new RuntimeException("Cannot clone a mutable processor");
        this.mutabilityHelper = new MutabilityHelper(this, true);
        this.typeData = oldProcessor.getTypeData();
        setUUID(ModelLoader.makeNewUUID());
    }

    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        String processorString = getXMLTag();
        if (processorString == null)
            processorString = "Processor";

        org.w3c.dom.Element element = doc.createElement(processorString);
        element.setAttribute("model", getModelIdentifier());
        element.setAttribute("UUID", getUUID().toString());
        element.appendChild(getTypeData().getXMLForDocument(doc));

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

    public Processor withTypeData(TypeData typeData) {
        if (isMutable()) {
            this.typeData = typeData;
            return this;
        } else {
            return createMutableClone().withTypeData(typeData);
        }
    }

    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }
}
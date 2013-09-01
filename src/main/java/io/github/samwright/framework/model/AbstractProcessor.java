package io.github.samwright.framework.model;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.helper.ModelLoader;
import io.github.samwright.framework.model.helper.MutabilityHelper;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Delegate;
import lombok.Getter;
import org.w3c.dom.Document;

import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 31/08/2013 Time: 12:53
 */
public abstract class AbstractProcessor implements Processor {

    @Delegate private final MutabilityHelper mutabilityHelper;
    @Getter private ModelController controller;
    @Getter private TypeData typeData;
    private UUID uuid;


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

    @Override
    public void setController(ModelController controller) {
        if (this.controller != controller) {
            this.controller = controller;
            if (getNext() != null) {
                getNext().setController(controller);
            } else if (controller != null) {
                Processor currentVersion = getCurrentVersion();
                if (currentVersion != null) {
                    controller.proposeModel(getCurrentVersion());
                    controller.handleUpdatedModel();
                } else if (!isMutable())
//                    controller.proposeModel(this);
                    setAsCurrentVersion();

            }
        }
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

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        if (getUUID() == null || !getUUID().equals(uuid)) {
            this.uuid = uuid;
            if (getNext() != null)
                getNext().setUUID(uuid);
            else {
                Processor currentVersion = getCurrentVersion();
                if (currentVersion == null) {
                    if (!isMutable())
                        setAsCurrentVersion();
                } else if (currentVersion.getUUID().equals(getUUID())) {
                    currentVersion.setAsCurrentVersion();
                }
            }
        }
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
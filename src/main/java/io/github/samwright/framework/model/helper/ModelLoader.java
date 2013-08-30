package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 26/08/2013 Time: 18:55
 */
public class ModelLoader {
    public static final class ModelLoadException extends RuntimeException {
        public ModelLoadException() {
        }

        public ModelLoadException(String message) {
            super(message);
        }

        public ModelLoadException(String message, Throwable cause) {
            super(message, cause);
        }

        public ModelLoadException(Throwable cause) {
            super(cause);
        }
    }

    private ModelLoader() {}

    private static Map<UUID,Processor> processors = new HashMap<>();
    private static Map<String,Processor> prototypeModels = new HashMap<>();

    public static void registerProcessor(Processor processor) {
        if (processor.isMutable())
            throw new ModelLoadException("Cannot register mutable processor");
        if (processor.getUUID() == null)
            throw new RuntimeException("Cannot register processor without UUID set");

        processors.put(processor.getUUID(), processor);
    }

    public static Processor getProcessor(UUID UUID) {
        return processors.get(UUID);
    }

    public static Collection<Processor> getAllProtoypeModels() {
        return prototypeModels.values();
    }

    public static UUID makeNewUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(processors.containsKey(uuid));

        return uuid;
    }

    public static void registerPrototypeModel(Processor prototypeModel) {
        if (prototypeModel.isMutable())
            throw new ModelLoadException("Cannot register mutable processor");
        prototypeModels.put(prototypeModel.getModelIdentifier(), prototypeModel);
    }

    public static Processor getPrototypeModel(Element xmlNode) {
        // 1. Get modelClassName from tag
        String modelClassName = xmlNode.getAttribute("model");

        // 2. Get corresponding model, or throw exception if not registered.
        Processor prototypeModel = prototypeModels.get(modelClassName);
        if (prototypeModel == null)
            throw new ModelLoadException("Model " + modelClassName + " has not been registered");

        return prototypeModel;
    }

    public static Processor loadProcessor(Element xmlNode, boolean useExistingIfPossible) {
        // Check if processor already loaded
        if (useExistingIfPossible) {
            String loadedUUIDString = xmlNode.getAttribute("UUID");
            UUID loadedUUID = UUID.fromString(loadedUUIDString);
            Processor existingProcessor = getProcessor(loadedUUID);
            if (existingProcessor != null)
                return existingProcessor;
        }

        Processor prototypeModel = getPrototypeModel(xmlNode);
        Processor model = prototypeModel.withXML(xmlNode, new HashMap<UUID, Processor>());
        prototypeModel.replaceWith(model);
//        model.discardPrevious();
        prototypeModel.discardNext();

        return (Processor) model.versionInfo().getLatest();
    }

}

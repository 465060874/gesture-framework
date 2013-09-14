package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The class with which Processors are registered, and from which Processors can be loaded.
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

    /**
     * Registers this {@link Processor} as the current version for its UUID.
     *
     * @param processor the processor to register.
     */
    public static void registerProcessor(Processor processor) {
        if (processor.getUUID() == null)
            throw new RuntimeException("Cannot register processor without UUID set");

        processors.put(processor.getUUID(), processor);
    }

    /**
     * Gets the current version of the {@link Processor} with the supplied UUID.
     *
     * @param UUID the UUID to get the current {@code Processor} for.
     * @return the current version of the {@code Processor} with the supplied UUID.
     */
    public static Processor getProcessor(UUID UUID) {
        return processors.get(UUID);
    }

    /**
     * Get all registered prototype models.  These are the {@link Processor} objects which are
     * cloned when a model needs to be loaded.
     *
     * @return all registered prototype models.
     */
    public static Collection<Processor> getAllProtoypeModels() {
        return prototypeModels.values();
    }

    /**
     * Returns a UUID that is guaranteed not to already be in use.
     *
     * @return a unique UUID.
     */
    public static UUID makeNewUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(processors.containsKey(uuid));

        return uuid;
    }

    /**
     * Register a prototype model against its identifier (ie.
     * {@code processor.getModelIdentifier()} ).
     * <p/>
     * When asked to load a model with the same model identifier, the supplied prototype will be
     * cloned and returned.
     *
     * @param prototypeModel the prototype model to register.
     */
    public static void registerPrototypeModel(Processor prototypeModel) {
        if (prototypeModel.isMutable())
            throw new ModelLoadException("Cannot register mutable processor");
        prototypeModels.put(prototypeModel.getModelIdentifier(), prototypeModel);
    }

    /**
     * Gets the prototype model with the same model identifier as the supplied XML
     * {@link Element}.
     *
     * @param xmlNode the node from which to load a model identifier.
     * @return the prototype model registered with the model identifier.
     */
    public static Processor getPrototypeModel(Element xmlNode) {
        return getPrototypeModel(xmlNode.getAttribute("model"));
    }

    /**
     * Gets the prototype model registered under the given model identifier.
     *
     * @param modelIdentifier the model identifier to find the prototype model for.
     * @return the prototype model registered under the given model identifier.
     */
    public static Processor getPrototypeModel(String modelIdentifier) {
        Processor prototypeModel = prototypeModels.get(modelIdentifier);
        if (prototypeModel == null)
            throw new ModelLoadException("Model " + modelIdentifier + " has not been registered");
        return prototypeModel;
    }

    /**
     * Loads a processor from the given XML {@link Element}, optionally using an already-loaded
     * {@link Processor} (otherwise a clone is created).
     *
     * @param xmlNode the XML {@code Element} from which to load the {@code Processor}.
     * @param useExistingIfPossible if true, will return an already-existing {@code Processor}
     *                              with the same UUID. Otherwise a new version is created.
     * @return the loaded {@code Processor}.
     */
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
        model.replace(null);
        model.setController(prototypeModel.getController().createClone());

//        while (model.getNext() != null)
        model = model.getCurrentVersion();
        model.discardPrevious();

        return model.getCurrentVersion();
    }

}

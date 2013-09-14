package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.Workflow;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.UUID;

/**
 * An object describing the input and output types of {@link Processor} objects.
 */
@AllArgsConstructor()
@EqualsAndHashCode
final public class TypeData {

    @Getter static private final TypeData defaultType;

    static {
        defaultType = new TypeData(Object.class, Object.class);
    }

    @Getter @NonNull private final Class<?> inputType;
    @Getter @NonNull private final Class<?> outputType;

    @Override
    public String toString() {
        return "<" + inputType.getSimpleName() + "," + outputType.getSimpleName() + ">";
    }

    /**
     * Checks if the associated {@link Processor} can do no conversion (ie. its output is its
     * input, without any processing taking place) while still being valid.
     *
     * @return true if the input type can be casted to the output type.
     */
    @SuppressWarnings("unchecked")
    public boolean canBeEmptyContainer() {
        return outputType.isAssignableFrom(inputType);
    }

    /**
     * Checks if this {@code Processor} can come before the other {@code Processor}.
     *
     * @param other the {@code TypeData} of the other {@code Processor}.
     * @return true iff this output type can be casted to the other's input type.
     */
    @SuppressWarnings("unchecked")
    public boolean canComeBefore(TypeData other) {
        return other.inputType.isAssignableFrom(this.outputType);
    }

    /**
     * Checks if this {@code Processor} can come after the other {@code Processor}.
     *
     * @param other the {@code TypeData} of the other {@code Processor}.
     * @return true iff the other's output type can be casted to this input type.
     */
    @SuppressWarnings("unchecked")
    public boolean canComeAfter(TypeData other) {
        return this.inputType.isAssignableFrom(other.outputType);
    }

    /**
     * Checks if this {@code Processor} can be the last element inside the given
     * {@link Workflow workflow}.
     *
     * @param workflow the {@code TypeData} of the {@code Workflow}.
     * @return true iff this output type can be casted to the workflow's output type.
     */
    @SuppressWarnings("unchecked")
    public boolean canBeAtEndOfWorkflow(TypeData workflow) {
        return workflow.outputType.isAssignableFrom(this.outputType);
    }

    /**
     * Checks if this {@code Processor} can be the first element inside the given
     * {@link Workflow workflow}.
     *
     * @param workflow the {@code TypeData} of the {@code Workflow}.
     * @return true iff the workflow's input type can be casted to this input type.
     */
    @SuppressWarnings("unchecked")
    public boolean canBeAtStartOfWorkflow(TypeData workflow) {
        return this.inputType.isAssignableFrom(workflow.inputType);
    }

    /**
     * Serialises this to an XML {@link Element}.
     *
     * @param doc the {@link Document} to create the node from.
     * @return
     */
    public Element getXMLForDocument(Document doc) {
        Element node = doc.createElement("TypeData");
        XMLHelper.addDataUnderNode(node, "Input", inputType.getName());
        XMLHelper.addDataUnderNode(node, "Output", outputType.getName());
        return node;
    }

    /**
     * Load a new {@code TypeData} object from the given XML.
     *
     * @param node the XML from which to load.
     * @param dictionary the dictionary of old to new UUIDs.
     * @return the loaded {@code TypeData} object.
     */
    public TypeData withXML(Element node, Map<UUID, Processor> dictionary) {
        Element typeDataNode = XMLHelper.getFirstChildWithName(node, "TypeData");

        String inputString = XMLHelper.getDataUnderNode(typeDataNode, "Input");
        String outputString = XMLHelper.getDataUnderNode(typeDataNode, "Output");

        return new TypeData(loadClass(inputString), loadClass(outputString));
    }

    /**
     * Wrapper for {@code Class.forName(className)} that only throws unchecked exceptions.
     *
     * @param className the name of the class to load.
     * @return
     */
    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ModelLoader.ModelLoadException("Couldn't find class: " + className, e);
        }
    }
}

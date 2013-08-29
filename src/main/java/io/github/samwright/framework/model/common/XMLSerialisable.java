package io.github.samwright.framework.model.common;

import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.ModelLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 26/08/2013 Time: 19:06
 */
public interface XMLSerialisable<T extends XMLSerialisable<T>> {

    /**
     * Serialises this object into an XML node for the given document.
     * <p/>
     * It is for the calling object to decide where in the document to append the returned node.
     *
     * @return this object as an XML node.
     */
    Element getXMLForDocument(Document doc);

    /**
     * Creates a mutable clone of this object, with the data defined in the supplied XML
     * {@link Node} where UUIDs in the node are first looked-up in the supplied dictionary.  If
     * there is no match, the UUID is then looked-up in the {@link ModelLoader} class.
     * <p/>
     * When an object defined in the supplied {@code Node} is instantiated,
     * the new object will be given a new UUID, and an entry will be made in the supplied
     * dictionary to link from the old UUID and this new object.  Subsequent references to the old
     * UUIDs are then dereferenced using the dictionary.  UUIDs of external objects (which
     * are available to both the original and the new objects) are instead dereferenced in the
     * {@code ModelLoader.getProcessor(uuid)} method.  Other references are assumed to be
     * transient (eg. relating to controllers and will be recreated when needed) and thus ignored.
     * <p/>
     * It is therefore imperative that the new objects are created and the dictionary fully
     * populated before any dereferencing of UUIDs is attempted.
     *
     * @param node       the XML node containing the data to populate the new object with.
     * @param dictionary the dictionary to use for translating old UUIDs to new UUIDs.  In most
     *                   cases, this should be an empty dictionary (which the function then
     *                   populates).
     */
    T withXML(Element node, Map<UUID, Processor> dictionary);
}

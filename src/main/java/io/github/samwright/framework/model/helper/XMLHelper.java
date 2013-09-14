package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.Processor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Helper class containing useful methods for dealing with XML (de)serialisation.
 */
public class XMLHelper {

    private static DocumentBuilder docBuilder;

    static {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private XMLHelper() {}

    /**
     * Iterable class that goes through an {@link Element} object's children (not grandchildren,
     * great-grandchildren etc...), optionally selecting only those children with the supplied
     * tag name.
     */
    private static class ElementIterable implements Iterable<Element> {
        private static final String wildcard = "*";
        private Element next;
        private String name;

        private ElementIterable(Element node) {
            this(node, wildcard);
        }

        private ElementIterable(Element node, String name) {
            this.name = name;
            next = findNextMatch(node.getFirstChild());
        }

        private boolean isMatch(Node node) {
            return node.getNodeType() == Node.ELEMENT_NODE
                    && (name.equals(wildcard) || name.equals(node.getNodeName()));
        }

        private Element findNextMatch(Node node) {
            while (node != null && !isMatch(node))
                node = node.getNextSibling();
            return (Element) node;
        }

        @Override
        public Iterator<Element> iterator() {
            return new ElementIterator();
        }

        private class ElementIterator implements Iterator<Element> {
            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Element next() {
                Element toReturn = next;
                next = findNextMatch(next.getNextSibling());
                return toReturn;
            }

            @Override
            public void remove() {
                throw new RuntimeException("Not implemented");
            }
        }
    }

    /**
     * Adds data under the given node, ie:
     * <p/>
     * {@code
     * <Parent></Parent>>
     * }
     * <p/>
     * becomes...
     * <p/>
     * {@code
     * <Parent>
     *     <nodeString>
     *         nodeData
     *     </nodeString>
     * </Parent>
     * }
     *
     * @param parentNode the node to add the data to.
     * @param nodeString the name of the data node to put under the parent node.
     * @param nodeData the data to put in the data node.
     */
    public static void addDataUnderNode(Element parentNode, String nodeString, String nodeData) {
        // Create an element for this data type
        Element dataElement = parentNode.getOwnerDocument().createElement(nodeString);

        // Fill element with nodeData
        dataElement.appendChild(parentNode.getOwnerDocument().createTextNode(nodeData));

        parentNode.appendChild(dataElement);
    }

    /**
     * Gets the first child (not grandchild, great-grandchild, ...) with the specified name.
     *
     * @param node the node to look under.
     * @param name the name to match the child's tag name with.
     * @return the first child node with the supplied name.
     */
    public static Element getFirstChildWithName(Element node, String name) {
        Iterator<Element> iterator = new ElementIterable(node, name).iterator();
        if (!iterator.hasNext())
            throw new ModelLoader.ModelLoadException("Couldn't find child with name: " + name +
                    " under node " + node.getTagName());
        return iterator.next();
    }

    /**
     * Gets data under the given node, ie.
     * <p/>
     * {@code
     * <node>
     *     <nodeString>
     *         nodeData
     *     </nodeString>
     * </node>
     * }
     * <p/>
     * would return "nodeData".
     *
     * @param node the node to look for data under.
     * @param nodeString the data node to look for.
     * @return the data in the data node.
     */
    public static String getDataUnderNode(Element node, String nodeString) {
        return getFirstChildWithName(node, nodeString).getTextContent();
    }

    /**
     * Loads a {@link Processor} from a file with the given filename,
     * optionally using an already-loaded {@code Processor} if it has the same UUID.
     *
     * @param filename the location of the file from which to load the {@code Processor}.
     * @param useExistingIfPossible if true and there is already a {@code Processor} with the
     *                              UUID specified in the file, return the current version of
     *                              that {@code Processor}.  Otherwise create a new one from
     *                              the relevant prototype model in {@link ModelLoader}.
     * @return the loaded {@code Processor}.
     */
    public static Processor loadProcessorFromFile(String filename, boolean useExistingIfPossible) {
        Document doc = getDocumentFromInput(new InputSource(new File(filename).toURI().toASCIIString()));
        return loadProcessorFromDocument(doc, useExistingIfPossible);
    }

    /**
     * Loads a {@link Processor} from a XML string optionally using an already-loaded {@code
     * Processor} if it has the same UUID.
     *
     * @param xml the XML string from which to load the {@code Processor}.
     * @param useExistingIfPossible if true and there is already a {@code Processor} with the
     *                              UUID specified in the string, return the current version of
     *                              that {@code Processor}.  Otherwise create a new one from
     *                              the relevant prototype model in {@link ModelLoader}.
     * @return the loaded {@code Processor}.
     */
    public static Processor loadProcessorFromString(String xml, boolean useExistingIfPossible) {
        Document doc = getDocumentFromInput(new InputSource(new StringReader(xml)));
        return loadProcessorFromDocument(doc, useExistingIfPossible);
    }

    /**
     * Serialise the given {@link Processor} into an XML string.
     *
     * @param processor the {@code Processor} to serialise.
     * @return the XML representation of the given object as a string.
     */
    public static String writeProcessorToString(Processor processor) {
        StringWriter stringWriter = new StringWriter();
        writeProcessorToResult(processor, new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

    /**
     * Serialise the given {@link Processor} into an XML file.
     *
     * @param processor the {@code Processor} to serialise.
     * @param filename the location to create the XML file.
     */
    public static void writeProcessorToFile(Processor processor, String filename) {
        writeProcessorToResult(processor, new StreamResult(new File(filename)));
    }

    private static Document createDocument() {
        return docBuilder.newDocument();
    }

    private static Processor loadProcessorFromDocument(Document doc, boolean useExistingIfPossible) {
        return ModelLoader.loadProcessor(doc.getDocumentElement(), useExistingIfPossible);
    }

    /**
     * Return an iterator for use in a for-each loop that will iterate through the given
     * {@link Element} object's children (not grandchildren, great-grandchildren, ...).
     *
     * @param node the node whose children will be iterated through.
     * @return an iterator to go through the node's children.
     */
    public static Iterable<Element> iterator(Element node) {
        return new ElementIterable(node);
    }

    /**
     * Return an iterator for use in a for-each loop that will iterate through the given
     * {@link Element} object's children (not grandchildren, great-grandchildren,
     * ...) that have the supplied name.
     *
     * @param node the node whose children will be iterated through.
     * @param name the required name to look for children with.
     * @return an iterator to go through the node's children.
     */
    public static Iterable<Element> iterator(Element node, String name) {
        return new ElementIterable(node, name);
    }

    /**
     * Gets the first child of a node.
     *
     * @param node the node to get the first child of.
     * @return the first child of the given node.
     */
    public static Element getFirstChild(Element node) {
        return getFirstChildWithName(node, "*");
    }

    private static Document getDocumentFromInput(InputSource inputSource) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc;

        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ModelLoader.ModelLoadException(e);
        }

        return doc;
    }

    private static void writeProcessorToResult(Processor processor, StreamResult streamResult) {
        Document doc = createDocument();
        doc.appendChild(processor.getXMLForDocument(doc));

        // Create transformer, which converts 'doc' into xml
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new ModelLoader.ModelLoadException(e);
        }

        // Make the xml nicely formatted (with multiple lines and indentation)
        // (taken from 'http://stackoverflow.com/questions/5142632/java-dom-xml-file-create-have-no-tabs-or-whitespaces-in-output-file')
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Prepare input to transformer (ie. doc)
        DOMSource source = new DOMSource(doc);

        // Perform the transformation
        try {
            transformer.transform(source, streamResult);
        } catch (TransformerException e) {
            throw new ModelLoader.ModelLoadException(e);
        }
    }
}

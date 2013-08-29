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
 * User: Sam Wright Date: 27/08/2013 Time: 12:18
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

    public static void addDataUnderNode(Element parentNode, String nodeString, String nodeData) {
        // Create an element for this data type
        Element dataElement = parentNode.getOwnerDocument().createElement(nodeString);

        // Fill element with nodeData
        dataElement.appendChild(parentNode.getOwnerDocument().createTextNode(nodeData));

        parentNode.appendChild(dataElement);
    }

    public static Element getFirstChildWithName(Element node, String name) {
        Iterator<Element> iterator = new ElementIterable(node, name).iterator();
        if (!iterator.hasNext())
            throw new ModelLoader.ModelLoadException("Couldn't find child with name: " + name +
                    " under node " + node.getTagName());
        return iterator.next();
    }

    public static String getDataUnderNode(Element node, String nodeString) {
        return getFirstChildWithName(node, nodeString).getTextContent();
    }

    public static Processor loadProcessorFromFile(String filename, boolean useExistingIfPossible) {
        Document doc = getDocumentFromInput(new InputSource(new File(filename).toURI().toASCIIString()));
        return loadProcessorFromDocument(doc, useExistingIfPossible);
    }

    public static Processor loadProcessorFromString(String xml, boolean useExistingIfPossible) {
        Document doc = getDocumentFromInput(new InputSource(new StringReader(xml)));
        return loadProcessorFromDocument(doc, useExistingIfPossible);
    }

    public static String writeProcessorToString(Processor processor) {
        StringWriter stringWriter = new StringWriter();
        writeProcessorToResult(processor, new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }

    public static void writeProcessorToFile(Processor processor, String filename) {
        writeProcessorToResult(processor, new StreamResult(new File(filename)));
    }

    private static Document createDocument() {
        return docBuilder.newDocument();
    }

    private static Processor loadProcessorFromDocument(Document doc, boolean useExistingIfPossible) {
        return ModelLoader.loadProcessor(doc.getDocumentElement(), useExistingIfPossible);
    }

    public static Iterable<Element> iterator(Element node) {
        return new ElementIterable(node);
    }

    public static Iterable<Element> iterator(Element node, String name) {
        return new ElementIterable(node, name);
    }

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

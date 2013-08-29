package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 21/08/2013 Time: 12:11
 */
public class ExElement extends AbstractElement {

    @Getter private int clicks;
    @Getter private String modelIdentifier;

    public ExElement(String modelIdentifier) {
        clicks = 0;
        this.modelIdentifier = "ExElement:" + modelIdentifier;
    }

    public ExElement(ExElement oldElement) {
        super(oldElement);
        this.clicks = oldElement.getClicks();
        this.modelIdentifier = oldElement.getModelIdentifier();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Mediator process(Mediator input) {
        return null; // Dummy implementation
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        return null; // Dummy implementation
    }

    @Override
    public ExElement createMutableClone() {
        return new ExElement(this);
    }

    public ExElement withClicks(int newClicks) {
        if (isMutable()) {
            this.clicks = newClicks;
            return this;
        } else {
            ExElement clone = createMutableClone();
            clone.withClicks(newClicks);
            return clone;
        }
    }

    @Override
    public Element getXMLForDocument(Document doc) {
        Element node = super.getXMLForDocument(doc);
        XMLHelper.addDataUnderNode(node, "clicks", String.valueOf(clicks));
        return node;
    }

    @Override
    public ExElement withXML(Element node, Map<UUID, Processor> map) {
        ExElement clone = (ExElement) super.withXML(node, map);
        if (clone != this)
            return clone;

        withClicks(Integer.parseInt(XMLHelper.getDataUnderNode(node, "clicks")));
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "("+clicks+")";
    }
}

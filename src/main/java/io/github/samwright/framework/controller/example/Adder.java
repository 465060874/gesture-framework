package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User: Sam Wright Date: 21/08/2013 Time: 12:11
 */
public class Adder extends AbstractElement {

    @Getter private int clicks;

    public Adder() {
        super(new TypeData(Integer.class, Integer.class));
        clicks = 0;
    }

    public Adder(Adder oldElement) {
        super(oldElement);
        this.clicks = oldElement.getClicks();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Mediator process(Mediator input) {
        Integer castedInput = (Integer) input.getData();
        return input.createNext(this, castedInput + clicks);
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        List<Mediator> outputs = new LinkedList<>();
        for (Mediator m : inputs)
            outputs.add(process(m));

        return outputs;
    }

    @Override
    public Adder createMutableClone() {
        return new Adder(this);
    }

    public Adder withClicks(int newClicks) {
        if (isMutable()) {
            this.clicks = newClicks;
            return this;
        } else {
            Adder clone = createMutableClone();
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
    public Adder withXML(Element node, Map<UUID, Processor> map) {
        Adder clone = (Adder) super.withXML(node, map);
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

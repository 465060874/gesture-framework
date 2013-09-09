package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 17:46
 */
public class StartElement extends AbstractElement {

    public StartElement() {
        super(new TypeData(StartType.class, Integer.class));
    }

    public StartElement(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        return Mediator.createEmpty().createNext(this, 0);
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        List<Mediator> outputs = new LinkedList<>();
        for (int i = 0; i < 3; ++i)
            outputs.add(Mediator.createEmpty().createNext(this, i));

        return outputs;
    }

    @Override
    public Element createMutableClone() {
        return new StartElement(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}

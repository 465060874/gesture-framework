package io.github.samwright.framework.model.mock;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;

import java.util.List;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 10:36
 */
public class MockElement extends AbstractElement {

    public MockElement() {
        super();
    }

    public MockElement(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mediator process(Mediator input) {
        return input.createNext(this, input.getData() + "1");
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Element createMutableClone() {
        return new MockElement(this);
    }
}

package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;

import java.util.List;

/**
 * User: Sam Wright Date: 21/08/2013 Time: 12:11
 */
public class ExampleModel extends AbstractElement {
    public ExampleModel() {
    }

    public ExampleModel(AbstractElement oldElement) {
        super(oldElement);
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
    public Element createMutableClone() {
        return new ExampleModel(this);
    }
}

package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;

/**
 * User: Sam Wright Date: 03/09/2013 Time: 17:59
 */
public class Viewer extends AbstractElement {

    private Integer previousOutput;

    public Viewer() {
        super(new TypeData(Integer.class, Integer.class));
    }

    public Viewer(AbstractElement oldElement) {
        super(oldElement);
    }

    @Override
    public Mediator process(Mediator input) {
        previousOutput = (Integer) input.getData();
        return input;
    }

    @Override
    public List<Mediator> processTrainingBatch(List<Mediator> inputs) {
        return inputs;
    }

    @Override
    public Element createMutableClone() {
        return new Viewer(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public Integer getPreviousOutput() {
        return previousOutput;
    }
}

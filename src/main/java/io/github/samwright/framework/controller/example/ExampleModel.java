package io.github.samwright.framework.controller.example;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.helper.Mediator;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 21/08/2013 Time: 12:11
 */
public class ExampleModel extends AbstractElement {

    @Getter private int clicks;

    public ExampleModel() {
        clicks = 0;
    }

    public ExampleModel(ExampleModel oldElement) {
        super(oldElement);
        this.clicks = oldElement.getClicks();
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
    public ExampleModel createMutableClone() {
        return new ExampleModel(this);
    }

    public ExampleModel withClicks(int newClicks) {
        if (isMutable()) {
            this.clicks = newClicks;
            return this;
        } else {
            ExampleModel clone = createMutableClone();
            clone.withClicks(newClicks);
            return clone;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "("+clicks+")";
    }
}

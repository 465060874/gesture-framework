package io.github.samwright.framework.model.mock;

import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

import java.util.List;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 10:36
 */
public class MockElement extends AbstractElement<String, String> {

    public MockElement(TypeData<String, String> typeData, String suffixToAdd) {
        super(typeData);
    }

    public MockElement(AbstractElement<?, ?> oldElement, TypeData<String, String> typeData) {
        super(oldElement, typeData);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mediator<String> process(Mediator<?> input) {
        Mediator<String> castedInput = (Mediator<String>) input;
        return input.createNext(this, castedInput.getData() + "1");
    }

    @Override
    public List<Mediator<String>> processTrainingBatch(List<Mediator<?>> inputs) {
        throw new RuntimeException("not implemented");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I2, O2> Element<I2, O2> withTypeData(TypeData<I2, O2> newTypeData) {
        return (Element<I2, O2>) new MockElement(this, (TypeData<String, String>) newTypeData);
    }
}

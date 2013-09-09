package io.github.samwright.framework.model.helper.mock;

import io.github.samwright.framework.model.AbstractProcessor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 13:00
 */
public class MockProcessor extends AbstractProcessor {

    public MockProcessor() {
    }

    public MockProcessor(AbstractProcessor oldProcessor) {
        super(oldProcessor);
    }

    @Override
    public boolean isValid() {
        return false; // Dummy implementation
    }

    @Override
    public Mediator process(Mediator input) {
        return null; // Dummy implementation
    }

    @Override
    public TypeData getTypeData() {
        return TypeData.getDefaultType();
    }

    @Override
    public MockProcessor createMutableClone() {
        return new MockProcessor(this);
    }

    @Override
    public void afterReplacement() {
        // Dummy implementation
    }

    @Override
    public String getXMLTag() {
        return null; // Dummy implementation
    }
}

package io.github.samwright.framework.javacv.viewers;

import io.github.samwright.framework.controller.helper.DataViewer;
import io.github.samwright.framework.controller.helper.StringViewer;
import io.github.samwright.framework.model.datatypes.Classification;
import io.github.samwright.framework.model.helper.Mediator;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 20:17
 */
public class ClassificationViewer extends StringViewer {

    @Override
    public String getString(Mediator mediator) {
        Classification classification = (Classification) mediator.getData();
        return classification.getTag();
    }

    @Override
    public DataViewer createClone() {
        return new ClassificationViewer();
    }

    @Override
    public Class<?> getViewableClass() {
        return Classification.class;
    }

    @Override
    public String toString() {
        return "Classification";
    }
}

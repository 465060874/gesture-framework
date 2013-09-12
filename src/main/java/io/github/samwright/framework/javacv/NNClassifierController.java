package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 18:29
 */
public class NNClassifierController extends ElementController {

    public NNClassifierController() {
        super("/fxml/NNClassifier.fxml");
        proposeModel(new NNClassifier());
        setElementLink(new ElementLink());
    }

    public NNClassifierController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new NNClassifierController(this);
    }
}

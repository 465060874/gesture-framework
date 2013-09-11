package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 17:37
 */
public class PalmDetectorController extends ElementController {

    public PalmDetectorController() {
        super("/fxml/PalmDetector.fxml");
        proposeModel(new PalmDetector());
        setElementLink(new ElementLink());
    }

    public PalmDetectorController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new PalmDetectorController(this);
    }
}

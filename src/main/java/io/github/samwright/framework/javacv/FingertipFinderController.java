package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 17:39
 */
public class FingertipFinderController extends ElementController {

    public FingertipFinderController() {
        super("/fxml/FingertipFinder.fxml");
        proposeModel(new FingertipFinder());
        setElementLink(new ElementLink());
    }

    public FingertipFinderController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new FingertipFinderController(this);
    }
}

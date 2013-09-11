package io.github.samwright.framework.javacv.to_delete;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 13:57
 */
public class ConvexHullFinderController extends ElementController {

    public ConvexHullFinderController() {
        super("/fxml/ConvexHull.fxml");
        proposeModel(new ConvexHullFinder());
        setElementLink(new ElementLink());
    }

    public ConvexHullFinderController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new ConvexHullFinderController(this);
    }
}

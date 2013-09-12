package io.github.samwright.framework.javacv.to_delete;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 09:28
 */
public class DominantPointFinderController extends ElementController {


    public DominantPointFinderController() {
        super("/fxml/DominantPointFinder.fxml");
        proposeModel(new DominantPointFinder());
        setElementLink(new ElementLink());
    }

    public DominantPointFinderController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new DominantPointFinderController();
    }
}

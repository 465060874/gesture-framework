package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.helper.ElementLink;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 18:31
 */
public class FingertipReducerController extends ElementController {

    public FingertipReducerController() {
        super("/fxml/FingertipReducer.fxml");
        proposeModel(new FingertipReducer());
        setElementLink(new ElementLink());
    }

    public FingertipReducerController(ElementController toClone) {
        super(toClone);
    }

    @Override
    public ElementController createClone() {
        return new FingertipReducerController(this);
    }
}

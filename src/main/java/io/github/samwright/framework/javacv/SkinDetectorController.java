package io.github.samwright.framework.javacv;

import io.github.samwright.framework.controller.WorkflowContainerControllerImpl;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.WorkflowContainer;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 22:45
 */
public class SkinDetectorController extends WorkflowContainerControllerImpl {

    public SkinDetectorController() {
//        super("/fxml/SkinDetector.fxml");
        WorkflowContainer model = new SkinDetector();
//        model = model.withChildren(Arrays.asList((Workflow)new WorkflowImpl(),
//                new WorkflowImpl()));
//        model.replace(null);
        proposeModel(model);
        setElementLink(new ElementLink());
    }

    public SkinDetectorController(WorkflowContainerControllerImpl toClone) {
        super(toClone);
    }

    @Override
    public SkinDetectorController createClone() {
        return new SkinDetectorController(this);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if (selected) {

        } else {

        }
    }
}

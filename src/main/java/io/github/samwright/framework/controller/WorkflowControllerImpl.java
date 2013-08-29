package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowImpl;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * User: Sam Wright Date: 18/07/2013 Time: 11:55
 */
public class WorkflowControllerImpl extends WorkflowController {

    @FXML
    private VBox workflow;

    @FXML
    private HBox elementsBox;

    @Getter private ElementLink defaultElementLink;


    public WorkflowControllerImpl() {
        super("/fxml/Workflow.fxml");
        setModel(new WorkflowImpl());
        defaultElementLink = new ElementLink();
    }

    public WorkflowControllerImpl(WorkflowControllerImpl toClone) {
        super(toClone);
        defaultElementLink = toClone.getDefaultElementLink().createClone();
    }

    @Override
    public void setDefaultElementLink(ElementLink defaultElementLink) {
        if (this.defaultElementLink != defaultElementLink) {
            this.defaultElementLink = defaultElementLink;

            if (this.defaultElementLink != null)
                MainWindowController.getTopController().handleUpdatedModel();
        }
    }

    @Override
    public ModelController<Workflow> createClone() {
        return new WorkflowControllerImpl(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        elementsBox.getChildren().clear();

        if (defaultElementLink != null)
            elementsBox.getChildren().add(defaultElementLink);

        for (Element element : getModel().getChildren()) {
            ElementController controller = (ElementController) element.getController();
            elementsBox.getChildren().add(controller);
            elementsBox.getChildren().add(controller.getElementLink());
        }
    }
}

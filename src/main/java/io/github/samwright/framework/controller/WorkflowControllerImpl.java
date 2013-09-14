package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.WorkflowImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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

    @FXML @Getter
    private FlowPane header;

    @FXML
    private Label label;


    @Getter private ElementLink defaultElementLink;


    public WorkflowControllerImpl() {
        super("/fxml/Workflow.fxml");
        defaultElementLink = new ElementLink();
        proposeModel(new WorkflowImpl());
        handleUpdatedModel();
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
                handleUpdatedModel();
        }
    }

    @Override
    public ModelController createClone() {
        return new WorkflowControllerImpl(this);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        elementsBox.getChildren().clear();

        if (defaultElementLink != null) {
            elementsBox.getChildren().add(defaultElementLink);
            defaultElementLink.setInputType(getModel().getTypeData().getInputType());

            Class linkOutput;
            if (getModel().getChildren().isEmpty())
                linkOutput = getModel().getTypeData().getOutputType();
            else
                linkOutput = getModel().getChildren().get(0).getTypeData().getInputType();

            defaultElementLink.setOutputType(linkOutput);
        }

        ElementController previous = null;

        for (Element element : getModel().getChildren()) {
            ElementController controller = (ElementController) element.getController();
            elementsBox.getChildren().add(controller);
            elementsBox.getChildren().add(controller.getElementLink());
            controller.getElementLink().setValid(true);

            controller.getElementLink().setInputType(element.getTypeData().getOutputType());
            if (previous != null)
                previous.getElementLink().setOutputType(element.getTypeData().getInputType());

            previous = controller;
        }

        if (previous != null)
            previous.getElementLink().setOutputType(getModel().getTypeData().getOutputType());

        defaultElementLink.setValid(true);

        for (Element element : getModel().getInvalidlyOrderedElements()) {
            if (element == null)
                defaultElementLink.setValid(false);
            else {
                ElementController controller = (ElementController) element.getController();
                controller.getElementLink().setValid(false);
            }
        }
    }
}

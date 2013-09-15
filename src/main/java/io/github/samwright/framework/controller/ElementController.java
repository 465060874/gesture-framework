package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.XMLHelper;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 22:53
 */
abstract public class ElementController extends JavaFXController {

    public final static DataFormat dataFormat
            = new DataFormat("io.github.samwright.framework.model.Element");

    private Map<Node, Pane> configNodesAndParent = new HashMap<>();
    @Getter private ElementLink elementLink;

    {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (getModel() == null)
                    throw new RuntimeException("handled node has no model");

                ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
                TransferMode transferMode;

                if (toolbox.getChildren().contains(ElementController.this)) {
                    transferMode = TransferMode.COPY;
                } else {
                    if (mouseEvent.isAltDown() || mouseEvent.isSecondaryButtonDown())
                        transferMode = TransferMode.COPY;
                    else {
                        transferMode = TransferMode.MOVE;
                        setBeingDragged(true);
                        elementLink.setBeingDragged(false);
                    }
                }

                Dragboard db = startDragAndDrop(transferMode);
                ClipboardContent cb = new ClipboardContent();
                cb.put(dataFormat, XMLHelper.writeProcessorToString(getModel()));
                db.setContent(cb);

                mouseEvent.consume();
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                setBeingDragged(false);
                dragEvent.consume();
            }
        });

        setElementLink(new ElementLink());
        setSelected(false);
    }

    public ElementController(String fxmlResource) {
        super(fxmlResource);
    }

    public ElementController(ElementController toClone) {
        super(toClone);
    }

    public void setElementLink(ElementLink elementLink) {
        this.elementLink = elementLink;
        if (elementLink != null)
            elementLink.setController(this);
    }

    @Override
    abstract public ElementController createClone();

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);
        getElementLink().setBeingDragged(beingDragged);
    }

    @Override
    public Element getModel() {
        return (Element) super.getModel();
    }

    @Override
    public void proposeModel(Processor proposedModel) {
        if (!(proposedModel instanceof Element))
            throw new RuntimeException("ElementController can only control Elements - not " +
                    proposedModel);
        super.proposeModel(proposedModel);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        for (Map.Entry<Node,Pane> e : configNodesAndParent.entrySet()) {
            Node configNode = e.getKey();
            Pane parent = e.getValue();

            boolean configVisible = parent.getChildren().contains(configNode);
            if (configVisible && !selected)
                parent.getChildren().remove(configNode);
            else if (!configVisible && selected)
                parent.getChildren().add(0, configNode);
        }
    }

    public void addConfigNode(Node configNode) {
        if (configNodesAndParent.containsKey(configNode))
            return;

        if (configNode.getParent() == null)
            throw new RuntimeException("config node has no parent!");
        configNodesAndParent.put(configNode, (Pane) configNode.getParent());
        setSelected(isSelected());
    }

    @Override
    public void handleProcessedData(final Mediator processedData) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (elementLink != null)
                    elementLink.handleProcessedData(processedData);
            }
        });
    }
}

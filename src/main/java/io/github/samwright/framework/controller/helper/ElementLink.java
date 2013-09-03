package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.WorkflowController;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.XMLHelper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 13:56
 */
public class ElementLink extends Pane implements ElementObserver {

    @FXML
    private Line line;

    @FXML
    private Polygon triangle;

    @Getter private ElementController controller;

    @Getter @Setter private boolean beingDragged = false;

    public ElementLink() {
        Controllers.bindViewToController("/fxml/ElementLink.fxml", this);

        line.setStartX(0);
        line.endXProperty().bind(widthProperty());
        line.startYProperty().bind(heightProperty().divide(2));
        line.endYProperty().bind(heightProperty().divide(2));

        triangle.translateXProperty().bind(widthProperty().divide(2));
        triangle.translateYProperty().bind(heightProperty().divide(2));

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getDragboard().hasContent(ElementController.dataFormat) &&
                        !isBeingDragged())
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                dragEvent.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (!isBeingDragged()) {
                    setStyle("-fx-background-color: lightblue");
                    dragEvent.consume();
                }
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (!isBeingDragged()) {
                    setStyle("-fx-background-color: transparent");
                    dragEvent.consume();
                }
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (!dragEvent.getDragboard().hasContent(ElementController.dataFormat)
                        || isBeingDragged())
                    return;

                String xml = (String) dragEvent.getDragboard().getContent(ElementController.dataFormat);
                boolean useExistingIfPossible = dragEvent.getTransferMode() == TransferMode.MOVE;

                Element draggedElement =
                        (Element) XMLHelper.loadProcessorFromString(xml, useExistingIfPossible);
                Workflow parentModel = getWorkflowController().getModel(); //.getCurrentVersion();
                if (parentModel.getCurrentVersion() != parentModel)
                    throw new RuntimeException("Element link's controller points to not-current " +
                            "model");
                List<Element> newSiblings = new ArrayList<>(parentModel.getChildren());

                // If moving, make sure the dragged element is not already in the workflow.
                // If it's in another workflow, by adding it as a child here, it will be removed
                // from that old workflow.
                if (dragEvent.getTransferMode() == TransferMode.MOVE) {
                    if (draggedElement.getController() == controller) {
                        dragEvent.consume();
                        return;
                    }

                    newSiblings.remove(draggedElement);

                    Element orphanedElement = draggedElement.withParent(null);
                    if (orphanedElement != draggedElement) {

                        draggedElement.replaceWith(orphanedElement);
                        draggedElement = orphanedElement;
                    }
                }

                int newIndex;
                if (controller == null) {
                    newIndex = 0;
                } else {
                    int myIndex = newSiblings.indexOf(controller.getModel());
                    if (myIndex == -1)
                        throw new RuntimeException("Couldn't find my controller!!!");
                    newIndex = myIndex + 1;
                }
                newSiblings.add(newIndex, draggedElement);

                if (newSiblings.equals(parentModel.getChildren()))
                    return;

                parentModel.replaceWith(parentModel.withChildren(newSiblings));
                dragEvent.setDropCompleted(true);
                dragEvent.consume();
            }
        });
    }

    public void setController(ElementController controller) {
        this.controller = controller;
        if (controller != null && controller.getElementLink() != this)
            controller.setElementLink(this);
    }

    public WorkflowController getWorkflowController() {
        Parent parent = getParent();
        while (parent != null && !(parent instanceof WorkflowController))
            parent = parent.getParent();
        return (WorkflowController) parent;
    }

    public ElementLink createClone() {
        return new ElementLink();
    }

    @Override
    public void notify(Mediator mediator) {
        // Dummy implementation
    }

    @Override
    public void notify(List<Mediator> mediators) {
        // Dummy implementation
    }

    @Override
    public String toString() {
        return String.format("ElementLink: controller = %s, model = %s", controller,
                controller.getModel());
    }
}

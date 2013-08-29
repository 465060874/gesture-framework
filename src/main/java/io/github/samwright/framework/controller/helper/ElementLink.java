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

                Element draggedElement;
                String xml = (String) dragEvent.getDragboard().getContent(ElementController.dataFormat);
                boolean useExistingIfPossible = dragEvent.getTransferMode() == TransferMode.MOVE;

                System.out.println("Got xml from dragboard: \n"+xml);

                draggedElement = (Element) XMLHelper.loadProcessorFromString(xml, useExistingIfPossible);
                System.out.println("Decoded element: " + draggedElement);
                System.out.println(" === dragged element is mutable? " + draggedElement.isMutable
                        ());
                System.out.println(" === dragged element is latest? " + (draggedElement
                        .versionInfo().getLatest() == draggedElement));

                Workflow parentModel = getWorkflowController().getModel();
                List<Element> newSiblings = new ArrayList<>(parentModel.getChildren());
                System.out.println("parent workflow = " + parentModel.getController().hashCode());
                System.out.println("old siblings = " + newSiblings);

                // If moving, make sure the dragged element is not already in the workflow.
                // If it's in another workflow, by adding it as a child here, it will be removed
                // from that old workflow.
                if (dragEvent.getTransferMode() == TransferMode.MOVE) {
                    newSiblings.remove(draggedElement);
//                    Element previous
//                            = (Element) draggedElement.versionInfo().getPrevious();
//                    if (previous != null)
//                        newSiblings.remove(previous);
                }

                int newIndex;
                if (controller == null) {
                    newIndex = 0;
                } else {
                    System.out.println("controller.getModel() versionInfo = " +
                            controller.getModel().versionInfo());

                    int myIndex = newSiblings.indexOf(controller.getModel());
                    if (myIndex == -1)
                        throw new RuntimeException("Couldn't find my controller!!!");
                    newIndex = myIndex + 1;
                }
                System.out.println("after cull siblings = " + newSiblings);
                System.out.println("New element's index = " + newIndex);

                newSiblings.add(newIndex, draggedElement);
                System.out.println("New siblings = " + newSiblings);


                if (newSiblings.equals(parentModel.getChildren())) {
                    System.out.println("Skipping because no change to siblings");
                    return;
                }

                if (draggedElement.getParent() != null)
                    System.out.print(" from " + draggedElement.getParent()
                            .getController().hashCode());
                System.out.println(" to " + parentModel.getController().hashCode());

                parentModel.replaceWith(parentModel.withChildren(newSiblings));
                System.out.println("Final siblings = " + getWorkflowController().getModel().getChildren());

                System.out.println("Drag n Drop completed!");
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
        System.out.format("Parent of %s is %s%n", this, getParent());
        while (parent != null && !(parent instanceof WorkflowController)) {
            parent = parent.getParent();
            System.out.println("parent is " + parent);
        }
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
}

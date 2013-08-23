package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.WorkflowController;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.Mediator;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
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
                if (dragEvent.getDragboard().hasContent(ElementController.dataFormat))
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                dragEvent.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                setStyle("-fx-background-color: lightblue");
                dragEvent.consume();
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                setStyle("-fx-background-color: white");
                dragEvent.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (!dragEvent.getDragboard().hasContent(ElementController.dataFormat))
                    return;

                ElementController draggedElementController = (ElementController)
                        DragHandler.getDraggedNode();

                if (draggedElementController == controller) {
                    System.out.println("same controller!");
                    dragEvent.setDropCompleted(true);
                    dragEvent.consume();
                    return;
                }

                Parent parent = getParent();
                while (parent != null && !(parent instanceof WorkflowController))
                    parent = parent.getParent();

                if (parent == null)
                    throw new RuntimeException("Tried to drag to element not in a workflow");

                Workflow workflow = ((WorkflowController) parent).getModel();
                List<Element> newSiblings = new ArrayList<>(workflow.getChildren());

                System.out.println("Siblings before cull: " + newSiblings);
                for (Element sibling : newSiblings)
                    System.out.println("\t" + sibling.versionInfo());
                System.out.println("dragged element versionInfo: "
                        + draggedElementController.getModel().versionInfo());


                // If moving, make sure the dragged element is not already in the workflow.
                // If it's in another workflow, by adding it as a child here, it will be removed
                // from that old workflow.
                if (dragEvent.getTransferMode() == TransferMode.MOVE) {
                    newSiblings.remove(draggedElementController.getModel());
                    Element previous
                            = (Element) draggedElementController.getModel().versionInfo().getPrevious();
                    if (previous != null)
                        newSiblings.remove(previous);
                }



                int newIndex;
                if (controller == null) {
                    newIndex = 0;
                    System.out.println("Default element link");
                } else {
                    System.out.println("controller.getModel() versionInfo = " +
                            controller.getModel().versionInfo());

                    int myIndex = newSiblings.indexOf(controller.getModel());
                    if (myIndex == -1)
                        System.out.println("Couldn't find my controller!!!");
                    newIndex = myIndex + 1;
                    System.out.println("controller.getModel() = " + controller.getModel());
                }

                if (draggedElementController.getModel() == null)
                    throw new RuntimeException("Model is null!!!!");

                newSiblings.add(newIndex, draggedElementController.getModel());

                System.out.println("suggested siblings: " + newSiblings);

                List<Workflow> parents = new LinkedList<>();
                for (Element sibling : newSiblings)
                    parents.add(sibling.getParent());

                System.out.println("suggested siblings' parents: " + parents);

                workflow.replaceWith(workflow.withChildren(newSiblings));

                workflow = (Workflow) workflow.versionInfo().getNext();
                System.out.println("new siblings: " + workflow.getChildren());

                List<Workflow> newParents = new LinkedList<>();
                for (Element sibling : workflow.getChildren())
                    newParents.add(sibling.getParent());

                System.out.println("new siblings' parents: " + newParents);
                System.out.println("dragged element versionInfo = "
                        + draggedElementController.getModel().versionInfo());
                System.out.println("workflow versionInfo = " + workflow.versionInfo());

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

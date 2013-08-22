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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.Getter;

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

    public ElementLink() {
        Controllers.bindViewToController("/fxml/ElementLink.fxml", this);

        line.setStartX(0);
        line.endXProperty().bind(widthProperty());
        line.startYProperty().bind(heightProperty().divide(2));
        line.endYProperty().bind(heightProperty().divide(2));

        triangle.translateXProperty().bind(widthProperty().divide(2));
        triangle.translateYProperty().bind(heightProperty().divide(2));

        setOnDragOver(new DragOverHandler(this, ElementController.dataFormat));

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

                ElementController newElementController = (ElementController)
                        DragHandler.getDraggedNode();

//                Workflow workflow = controller.getModel().getParent();
                Parent parent = getParent();
                while (parent != null && !(parent instanceof WorkflowController))
                    parent = parent.getParent();

                if (parent == null)
                    throw new RuntimeException("Tried to drag to element link not in a workflow");

                Workflow workflow = ((WorkflowController) parent).getModel();
                List<Element> newSiblings = new ArrayList<>(workflow.getChildren());

                int myIndex;
                if (controller == null)
                    myIndex = -1;
                else
                    myIndex = newSiblings.indexOf(controller.getModel());

                if (newElementController.getModel() == null)
                    throw new RuntimeException("Model is null!!!!");

                newSiblings.add(myIndex + 1, newElementController.getModel());
                workflow.replaceWith(workflow.withChildren(newSiblings));


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

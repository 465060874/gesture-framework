package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.controller.ElementController;
import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.WorkflowController;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.XMLHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 13:56
 */
public class ElementLink extends Pane implements ElementObserver {

    @FXML
    private Line topLine, bottomLine, leftVertLine, leftHorizLine, rightVertLine, rightHorizLine;

    @FXML
    private Polygon topTriangle, bottomTriangle;

    @FXML
    private Label inputTypeLabel, outputTypeLabel;

    private Pane previewPane;
    @Getter private ElementController controller;
    @Getter @Setter private boolean beingDragged = false;

    public ElementLink() {
        Controllers.bindViewToController("/fxml/ElementLink.fxml", this);
        Pane pane = new Pane();
        pane.setMinWidth(50);
        pane.setMinHeight(50);
        pane.setPrefWidth(50);
        pane.setPrefHeight(50);
        pane.setStyle("-fx-background-color: RED");
        setPreviewPane(pane);
        setStyle("-fx-background-color: lightgreen");

        setValid(true);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                        && mouseEvent.getClickCount() == 2) {

                    if (previewPane != null) {
                        previewPane.setVisible(!previewPane.isVisible());
                        MainWindowController.getTopController().deselectAll();
                    }
                    mouseEvent.consume();
                }
            }
        });

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
                Workflow parentModel = getWorkflowController().getModel();
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

    public void setPreviewPane(@NonNull Pane previewPane) {
        if (this.previewPane != null)
            getChildren().remove(this.previewPane);
        this.previewPane = previewPane;
        getChildren().add(previewPane);

        NumberBinding previewPaneWidth = new When(previewPane.visibleProperty())
                .then(previewPane.widthProperty())
                .otherwise(0);

        NumberBinding previewPaneMinWidth = new When(previewPane.visibleProperty())
                .then(previewPane.minWidthProperty())
                .otherwise(0);

        NumberBinding previewPaneHeight = new When(previewPane.visibleProperty())
                .then(previewPane.heightProperty())
                .otherwise(0);

        NumberBinding previewPaneMinHeight = new When(previewPane.visibleProperty())
                .then(previewPane.minHeightProperty())
                .otherwise(0);

        topLine.startXProperty().bind(leftHorizLine.endXProperty());
        topLine.endXProperty().bind(rightHorizLine.startXProperty());
        topLine.startYProperty().bind(
                (heightProperty().subtract(previewPaneHeight))
                        .divide(2)
        );
        topLine.endYProperty().bind(topLine.startYProperty());

        topTriangle.translateYProperty().bind(topLine.startYProperty());
        topTriangle.translateXProperty().bind(
                inputTypeLabel.widthProperty().add(
                        (widthProperty()
                                .subtract(inputTypeLabel.widthProperty())
                                .subtract(outputTypeLabel.widthProperty())
                        ).divide(2))
        );

        bottomLine.startXProperty().bind(topLine.startXProperty());
        bottomLine.endXProperty().bind(topLine.endXProperty());
        bottomLine.startYProperty().bind(
                topLine.startYProperty().add(previewPaneHeight)
        );
        bottomLine.endYProperty().bind(bottomLine.startYProperty());

        bottomTriangle.translateYProperty().bind(bottomLine.startYProperty());
        bottomTriangle.translateXProperty().bind(topTriangle.translateXProperty());

        leftVertLine.visibleProperty().bind(previewPane.visibleProperty());
        rightVertLine.visibleProperty().bind(previewPane.visibleProperty());

        int padding = 10;

        leftHorizLine.setStartX(0);
        leftHorizLine.startYProperty().bind(heightProperty().divide(2));
        leftHorizLine.setEndX(padding);
        leftHorizLine.endYProperty().bind(leftHorizLine.startYProperty());

        rightHorizLine.startXProperty().bind(widthProperty().subtract(padding));
        rightHorizLine.startYProperty().bind(heightProperty().divide(2));
        rightHorizLine.endXProperty().bind(widthProperty());
        rightHorizLine.endYProperty().bind(rightHorizLine.startYProperty());

        leftVertLine.startXProperty().bind(topLine.startXProperty());
        leftVertLine.startYProperty().bind(topLine.startYProperty());
        leftVertLine.endXProperty().bind(bottomLine.startXProperty());
        leftVertLine.endYProperty().bind(bottomLine.startYProperty());

        rightVertLine.startXProperty().bind(topLine.endXProperty());
        rightVertLine.startYProperty().bind(topLine.endYProperty());
        rightVertLine.endXProperty().bind(bottomLine.endXProperty());
        rightVertLine.endYProperty().bind(bottomLine.endYProperty());


        inputTypeLabel.translateYProperty().bind(
                topLine.startYProperty().subtract(inputTypeLabel.heightProperty())
        );
        inputTypeLabel.translateXProperty().bind(topLine.startXProperty());

        outputTypeLabel.translateYProperty().bind(bottomLine.startYProperty());
        outputTypeLabel.translateXProperty().bind(
                bottomLine.endXProperty().subtract(outputTypeLabel.widthProperty())
        );

        minWidthProperty().bind(
                Bindings.max(
                        inputTypeLabel.widthProperty().add(outputTypeLabel.widthProperty()).add(30),
                        previewPaneMinWidth
                )
        );

        minHeightProperty().bind(
                inputTypeLabel.heightProperty().add(outputTypeLabel.heightProperty()).add(5)
                        .add(previewPaneMinHeight)
        );

        previewPane.translateXProperty().bind(
                (widthProperty().subtract(previewPaneWidth))
                        .divide(2)
        );

        previewPane.translateYProperty().bind(topLine.startYProperty());


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
        System.out.println("Link notified of: " + mediator.getData());
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

    public void setValid(boolean valid) {
        Paint triangleColour;

        if (valid)
            triangleColour = Paint.valueOf("DODGERBLUE");
        else
            triangleColour = Paint.valueOf("RED");

        topTriangle.setFill(triangleColour);
        bottomTriangle.setFill(triangleColour);
    }

    public void setInputType(Class input) {
        inputTypeLabel.setText(input.getSimpleName());
    }

    public void setOutputType(Class output) {
        outputTypeLabel.setText(output.getSimpleName());
    }
}

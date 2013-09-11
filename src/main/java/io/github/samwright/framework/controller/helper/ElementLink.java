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

    private static final String empty = " ";

    @FXML
    private Line mainLine, leftVertLine, leftHorizLine, rightVertLine, rightHorizLine;

    @FXML
    private Polygon triangle;

    @FXML
    private Label inputTypeLabel, outputTypeLabel;

    private PreviewPane previewPane;
    @Getter private ElementController controller;
    @Getter @Setter private boolean beingDragged = false;

    private DelayedNotifier<Mediator> mediatorNotifier = new DelayedNotifier<Mediator>() {
        @Override
        public void handleNewObject(Mediator newObject) {
            if (previewPane != null)
                previewPane.notify(newObject);
        }
    };
    private String inputTypeString, outputTypeString;
    private boolean valid;

    public ElementLink() {
        Controllers.bindViewToController("/fxml/ElementLink.fxml", this);
        PreviewPane pane = new PreviewPane();
        setPreviewPane(pane);
        pane.setVisible(false);
        setValid(true);

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                        && mouseEvent.getClickCount() == 2) {

                    if (previewPane != null && controller != null) {
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

    public void setPreviewPane(@NonNull PreviewPane previewPane) {
        if (this.previewPane != null)
            getChildren().remove(this.previewPane);
        this.previewPane = previewPane;
        getChildren().add(previewPane);

        double previewPaneBorder = 5;

        NumberBinding previewPaneWidth = new When(previewPane.visibleProperty())
                .then(previewPane.widthProperty())
                .otherwise(0)
                .add(2 * previewPaneBorder);

        NumberBinding previewPaneHeight = new When(previewPane.visibleProperty())
                .then(previewPane.heightProperty().add(2 * previewPaneBorder))
                .otherwise(0);

        mainLine.startXProperty().bind(previewPane.translateXProperty().subtract(previewPaneBorder));
        mainLine.endXProperty().bind(mainLine.startXProperty().add(previewPaneWidth));
        mainLine.startYProperty().bind(
                ((heightProperty().subtract(previewPaneHeight))
                        .divide(2))
                        .add(previewPaneHeight)
        );
        mainLine.endYProperty().bind(mainLine.startYProperty());

        triangle.translateYProperty().bind(mainLine.startYProperty());
        triangle.translateXProperty().bind(
                inputTypeLabel.widthProperty().add(
                        (widthProperty()
                                .subtract(inputTypeLabel.widthProperty())
                                .subtract(outputTypeLabel.widthProperty())
                        ).divide(2))
        );

        leftVertLine.visibleProperty().bind(previewPane.visibleProperty());
        rightVertLine.visibleProperty().bind(previewPane.visibleProperty());

        int padding = 10;

        leftHorizLine.setStartX(0);
        leftHorizLine.startYProperty().bind(heightProperty().divide(2));
        leftHorizLine.endXProperty().bind(mainLine.startXProperty());
        leftHorizLine.endYProperty().bind(leftHorizLine.startYProperty());

        rightHorizLine.startXProperty().bind(mainLine.endXProperty());
        rightHorizLine.startYProperty().bind(heightProperty().divide(2));
        rightHorizLine.endXProperty().bind(widthProperty());
        rightHorizLine.endYProperty().bind(rightHorizLine.startYProperty());

        leftVertLine.startXProperty().bind(mainLine.startXProperty());
        leftVertLine.startYProperty().bind(mainLine.startYProperty());
        leftVertLine.endXProperty().bind(leftVertLine.startXProperty());
        leftVertLine.endYProperty().bind(leftHorizLine.startYProperty());

        rightVertLine.startXProperty().bind(mainLine.endXProperty());
        rightVertLine.startYProperty().bind(mainLine.endYProperty());
        rightVertLine.endXProperty().bind(rightVertLine.startXProperty());
        rightVertLine.endYProperty().bind(rightHorizLine.startYProperty());


        inputTypeLabel.translateYProperty().bind(
                leftHorizLine.startYProperty()
                .subtract(inputTypeLabel.heightProperty())
        );
        inputTypeLabel.setTranslateX(0);

        outputTypeLabel.translateYProperty().bind(
                rightHorizLine.startYProperty()
                .subtract(outputTypeLabel.heightProperty())
        );
        outputTypeLabel.translateXProperty().bind(
                widthProperty()
                .subtract(outputTypeLabel.widthProperty())
        );

        minWidthProperty().bind(
                inputTypeLabel.widthProperty().add(outputTypeLabel.widthProperty()).add(10)
                .add(previewPaneWidth).add(2 * padding)
        );

        minHeightProperty().bind(
                Bindings.max(
                    Bindings.max(
                            outputTypeLabel.heightProperty(),
                            inputTypeLabel.heightProperty()
                    ),
                    previewPaneHeight
                ).add(10)

        );

        previewPane.translateXProperty().bind(
                triangle.translateXProperty()
                .subtract(previewPaneWidth.divide(2))
        );

        previewPane.translateYProperty().bind(
                mainLine.startYProperty()
                .subtract(previewPaneHeight)
        );
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
        mediatorNotifier.notify(mediator);
    }

    @Override
    public String toString() {
        return String.format("ElementLink: controller = %s, model = %s", controller,
                controller.getModel());
    }

    public void setValid(boolean valid) {
        Paint triangleColour;
        this.valid = valid;

        if (valid) {
            triangleColour = Paint.valueOf("DODGERBLUE");
            inputTypeLabel.setText(empty);
            outputTypeLabel.setText(empty);
        } else {
            triangleColour = Paint.valueOf("RED");
            inputTypeLabel.setText(inputTypeString);
            outputTypeLabel.setText(outputTypeString);
        }

        triangle.setFill(triangleColour);
    }

    public void setInputType(Class input) {
        inputTypeString = input.getSimpleName();
        if (valid)
            inputTypeLabel.setText(empty);
        else
            inputTypeLabel.setText(inputTypeString);
        if (previewPane != null)
            previewPane.setInputType(input);
    }

    public void setOutputType(Class output) {
        outputTypeString = output.getSimpleName();
        if (valid)
            outputTypeLabel.setText(empty);
        else
            outputTypeLabel.setText(outputTypeString);
    }
}

package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.controller.ToolboxController;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import lombok.Getter;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 11:52
 */
public class DragHandler implements EventHandler<MouseEvent> {

    @Getter private static ModelController draggedNode;

    public static void clearDraggedNode() {
        draggedNode = null;
    }


    private final ModelController handledNode;
    private final DataFormat dataFormat;

    public DragHandler(ModelController handledNode, DataFormat dataFormat) {
        this.handledNode = handledNode;
        this.dataFormat = dataFormat;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (draggedNode != null)
            throw new RuntimeException("Can only drag one object at a time...");

        if (handledNode.getModel() == null)
            throw new RuntimeException("handled node has no model");

        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        TransferMode transferMode;

        if (toolbox.getChildren().contains(handledNode)) {
            transferMode = TransferMode.COPY;
        } else {
            if (mouseEvent.isAltDown() || mouseEvent.isSecondaryButtonDown())
                transferMode = TransferMode.COPY;
            else
                transferMode = TransferMode.MOVE;
        }

        if (transferMode == TransferMode.COPY)
            draggedNode = handledNode.createClone();
        else
            draggedNode = handledNode;

        Dragboard db = handledNode.startDragAndDrop(transferMode);
        ClipboardContent cb = new ClipboardContent();
        cb.put(dataFormat, draggedNode.toString());
        db.setContent(cb);

        mouseEvent.consume();
    }
}

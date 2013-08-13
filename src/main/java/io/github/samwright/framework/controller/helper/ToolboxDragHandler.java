package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.controller.ModelController;
import javafx.event.EventHandler;
import javafx.scene.input.*;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 11:52
 */
public class ToolboxDragHandler implements EventHandler<MouseEvent> {

    public static final DataFormat controller = new DataFormat("New Controller");

    private final ModelController handledNode;

    public ToolboxDragHandler(ModelController handledNode) {
        this.handledNode = handledNode;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        Dragboard db = handledNode.startDragAndDrop(TransferMode.COPY);
        ClipboardContent cb = new ClipboardContent();
        cb.put(controller, handledNode.duplicate());
        db.setContent(cb);

        mouseEvent.consume();
    }
}

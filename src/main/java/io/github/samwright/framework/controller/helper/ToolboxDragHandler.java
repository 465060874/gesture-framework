package io.github.samwright.framework.controller.helper;

import io.github.samwright.framework.controller.ModelController;
import javafx.event.EventHandler;
import javafx.scene.input.*;

/**
 * User: Sam Wright Date: 29/07/2013 Time: 11:52
 */
public class ToolboxDragHandler implements EventHandler<MouseEvent> {

    private final ModelController handledNode;
    private final DataFormat dataFormat;

    public ToolboxDragHandler(ModelController handledNode, DataFormat dataFormat) {
        this.handledNode = handledNode;
        this.dataFormat = dataFormat;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        Dragboard db = handledNode.startDragAndDrop(TransferMode.COPY);
        ClipboardContent cb = new ClipboardContent();
        cb.put(dataFormat, handledNode.createClone());
        db.setContent(cb);

        mouseEvent.consume();
    }
}

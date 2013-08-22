package io.github.samwright.framework.controller.helper;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 20:20
 */
public class DragOverHandler implements EventHandler<DragEvent> {


    private final Node handledNode;
    private final DataFormat dataFormat;

    public DragOverHandler(Node handledNode, DataFormat dataFormat) {
        this.handledNode = handledNode;
        this.dataFormat = dataFormat;
    }

    @Override
    public void handle(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasContent(dataFormat))
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);

        dragEvent.consume();
    }
}

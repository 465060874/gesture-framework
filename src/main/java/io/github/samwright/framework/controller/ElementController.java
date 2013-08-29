package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.XMLHelper;
import javafx.event.EventHandler;
import javafx.scene.input.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright Date: 17/07/2013 Time: 22:53
 */
abstract public class ElementController extends ModelController<Element> {

    public final static DataFormat dataFormat
            = new DataFormat("io.github.samwright.framework.model.Element");

    @Getter private ElementLink elementLink;

    {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (getModel() == null)
                    throw new RuntimeException("handled node has no model");

                ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
                TransferMode transferMode;

                if (toolbox.getChildren().contains(ElementController.this)) {
                    transferMode = TransferMode.COPY;
                } else {
                    if (mouseEvent.isAltDown() || mouseEvent.isSecondaryButtonDown())
                        transferMode = TransferMode.COPY;
                    else {
                        transferMode = TransferMode.MOVE;
                        setBeingDragged(true);
                    }
                }

                System.out.println("Dragging model" + getModel().hashCode());
                System.out.println("Element being dragged = " + ElementController.this.hashCode());

                String xml = XMLHelper.writeProcessorToString(getModel());
                System.out.format("====%n%n%s%n%n====", xml);

                Dragboard db = startDragAndDrop(transferMode);
                ClipboardContent cb = new ClipboardContent();
                cb.put(dataFormat, xml);
                db.setContent(cb);

                mouseEvent.consume();
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                setBeingDragged(false);
                dragEvent.consume();
            }
        });
    }

    public ElementController(String fxmlResource) {
        super(fxmlResource);
    }

    public ElementController(ElementController toClone) {
        super(toClone);
        if (toClone.getElementLink() != null)
            setElementLink(toClone.getElementLink().createClone());
    }

    public void setElementLink(ElementLink elementLink) {

        ElementLink oldElementLink = this.elementLink;
        this.elementLink = elementLink;
        if (elementLink != null)
            elementLink.setController(this);

        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");

        if (getModel() != null && !toolbox.getChildren().contains(this)) {
            Set<ElementObserver> newObservers = new HashSet<>(getModel().getObservers());

            if (oldElementLink != null)
                newObservers.remove(oldElementLink);

            if (elementLink != null)
                newObservers.add(elementLink);

            Element newModel = getModel().withObservers(newObservers);
            getModel().replaceWith(newModel);
        }

    }

    @Override
    public void setModel(Element model) {
        super.setModel(model);
    }

    @Override
    abstract public ElementController createClone();

    @Override
    public void handleUpdatedModel() {
        if (elementLink != null && !getModel().getObservers().contains(elementLink))
            setElementLink(elementLink);
    }

    @Override
    public void setBeingDragged(boolean beingDragged) {
        super.setBeingDragged(beingDragged);
        getElementLink().setBeingDragged(beingDragged);
    }
}

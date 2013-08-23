package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.DragHandler;
import io.github.samwright.framework.controller.helper.ElementLink;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.common.ElementObserver;
import javafx.event.EventHandler;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
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
        setOnDragDetected(new DragHandler(this, ElementController.dataFormat));
        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
//                if (dragEvent.getTransferMode() == TransferMode.MOVE && dragEvent.isDropCompleted())
//                    getModel().delete();

                DragHandler.clearDraggedNode();
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

        if (elementLink != null && !model.getObservers().contains(elementLink))
            setElementLink(elementLink);
    }
}

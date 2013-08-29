package io.github.samwright.framework.controller;

import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Random;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends EventuallyImmutable> extends VBox {

    @Getter private T model;

    @Getter private final String fxmlResource;

    @Setter @Getter private boolean isBeingDragged = false;

    private String getRandomColor() {
        Random r = new Random();
        StringBuilder s = new StringBuilder("#");
        for (int n = 0; n < 6; ++n) {
            int i = r.nextInt(8) + 8;
            s.append(Integer.toHexString(i));
        }
        return s.toString();
    }

    {
        setStyle("-fx-background-color: " + getRandomColor());
    }

    public ModelController(@NonNull String fxmlResource) {
        this.fxmlResource = fxmlResource;
        Controllers.bindViewToController(fxmlResource, this);
    }

    @SuppressWarnings("unchecked")
    public ModelController(ModelController<T> toClone) {
        this(toClone.getFxmlResource());
        this.model = null;
//        if (toClone.getModel() != null) {

//            if (toClone.getModel().getController() == toClone) {
//            System.out.println("top-level copy of " + toClone.getModel());
//            T clonedModel = (T) toClone.getModel().createOrphanedDeepClone();
//            setModel(clonedModel);
//            } else {
//                System.out.println("supbordinate copy of " + toClone.getModel());
//            }
//        } else {
//            System.out.println("toClone model was null!");
//        }
    }

    public void setModel(T model) {
        if (this.model != model) {
            this.model = model;
            if (this.model.getController() != this)
                model.setController(this);
        }
    }

    abstract public ModelController<T> createClone();

    abstract public void handleUpdatedModel();
}

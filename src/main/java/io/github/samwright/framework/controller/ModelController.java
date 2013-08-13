package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.controller.helper.Controllers;
import io.github.samwright.framework.model.Processor;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;

/**
 * User: Sam Wright Date: 16/07/2013 Time: 12:25
 */
public abstract class ModelController<T extends Processor> extends VBox
        implements BeanNameAware {

    @Setter private String beanName;

    @Setter @Getter private T model;

    public ModelController(@NonNull String fxmlResource) {
        Controllers.bindViewToController(fxmlResource, this);
    }

    public ModelController<T> duplicate() {
        ModelController<T> duplicate = (ModelController<T>) MainApp.beanFactory.getBean(beanName);
        Processor duplicateModel = model.withTypeData(model.getTypeData());
        duplicateModel.setController(duplicate);
        return duplicate;
    }

    abstract public void handleUpdatedModel();
}

package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.WorkflowContainer;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 18:05
 */
public class TopContainerController extends WorkflowContainerControllerImpl {

    private MainWindowController mainWindow;

    {
        setOnDragDetected(null);
        setOnDragDone(null);
    }

    public TopContainerController(TopContainerController toClone) {
        super(toClone);
        this.mainWindow = toClone.mainWindow;
    }

    public TopContainerController(MainWindowController mainWindow) {
        super();
        setModel(new TopWorkflowContainer());
        this.mainWindow = mainWindow;
    }

    @Override
    public ModelController<Element> createClone() {
        return new TopContainerController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();
        mainWindow.handleUpdatedModel();
    }

    public void undo() {
        WorkflowContainer previousModel = (WorkflowContainer) getModel().versionInfo().getPrevious();
        getModel().undo();

        if (previousModel != getModel())
            throw new RuntimeException("Didn't undo properly!");
    }

    public void redo() {
        WorkflowContainer nextModel = (WorkflowContainer) getModel().versionInfo().getNext();
        getModel().redo();

        if (nextModel != getModel())
            throw new RuntimeException("Didn't redo properly!");
    }

    public boolean canRedo() {
        return getModel().versionInfo().getNext() != null;
    }

    public boolean canUndo() {
        return getModel().versionInfo().getPrevious() != null;
    }


}

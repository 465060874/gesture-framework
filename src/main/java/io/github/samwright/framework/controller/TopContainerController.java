package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.WorkflowContainer;

import java.util.Deque;
import java.util.LinkedList;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 18:05
 */
public class TopContainerController extends WorkflowContainerControllerImpl {

    private MainWindowController mainWindow;
    private Deque<WorkflowContainer> redoStack = new LinkedList<>();
    private boolean freezeRedoStack = false;

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
        System.out.println("New top model = " + getModel());

        if (getModel().versionInfo().getNext() != null)
            System.out.println("next version exists! " + getModel().versionInfo().getNext());

        if (!freezeRedoStack && !redoStack.isEmpty() && redoStack.peek() != getModel())
            redoStack.clear();

        mainWindow.handleUpdatedModel();
    }

    public void undo() {
        WorkflowContainer previousModel = (WorkflowContainer) getModel().versionInfo().getPrevious();
        if (previousModel == null)
            throw new RuntimeException("no undo available!");
        redoStack.push(getModel());
        if (getModel().versionInfo().getNext() != null)
            throw new RuntimeException("huh?");

        System.out.format("Undoing to %s from %s%n", previousModel, getModel());
        System.out.println("Redo stack after undo = " + redoStack);
        freezeRedoStack = true;
        previousModel.discardNext();
        freezeRedoStack = false;

        if (previousModel != getModel())
            throw new RuntimeException("Didn't undo properly!");
    }

    public void redo() {
        System.out.println("Redo stack before = " + redoStack);
        WorkflowContainer nextModel = redoStack.poll();
        System.out.format("Redoing from %s to %s%n", getModel(), nextModel);
        freezeRedoStack = true;
        getModel().replaceWith(nextModel);
        freezeRedoStack = false;
        System.out.println("Redo stack after = " + redoStack);

        if (nextModel != getModel())
            throw new RuntimeException("Didn't redo properly!");
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public boolean canUndo() {
        return getModel().versionInfo().getPrevious() != null;
    }
}

package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.model.TopWorkflowContainer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 18:05
 */
public class TopContainerController extends WorkflowContainerControllerImpl {

    private final Set<JavaFXController> selected = new HashSet<>();

    private MainWindowController mainWindow;
    private int transientUpdateMode = 0;
    private Object[] updateLock = new Object[0];
    private boolean needsUpdate = false;

    {
        setMaxHeight(Double.MAX_VALUE);
        setOnDragDetected(null);
        setOnDragDone(null);
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                deselectAll();
                mouseEvent.consume();
            }
        });
    }

    public TopContainerController(TopContainerController toClone) {
        super(toClone);
        this.mainWindow = toClone.mainWindow;
    }

    public TopContainerController(MainWindowController mainWindow) {
        super();
        proposeModel(new TopWorkflowContainer());

        this.mainWindow = mainWindow;
    }

    public void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
            Set<JavaFXController> oldSelected = new HashSet<>(selected);
            deselectAll();
            startTransientUpdateMode();
            try {
                for (ModelController toDelete : oldSelected)
                    toDelete.getModel().getCurrentVersion().delete();
            } finally {
                endTransientUpdateMode();
            }
        }
    }

    public void startTransientUpdateMode() {
        if (transientUpdateMode == 0 && getProposedModel() != null)
            getProposedModel().setTransientModel(true);
        ++transientUpdateMode;
    }

    public void endTransientUpdateMode() {
        --transientUpdateMode;
        if (getProposedModel() != null && transientUpdateMode == 0) {
            getProposedModel().setTransientModel(false);
            handleUpdatedModel();
        }
    }

    @Override
    public TopContainerController createClone() {
        return new TopContainerController(this);
    }

    @Override
    public void handleUpdatedModel() {
        if (Thread.holdsLock(updateLock)) {
            needsUpdate = true;
        } else {
            synchronized (updateLock) {
                needsUpdate = false;

                if (transientUpdateMode > 0) {
                    getProposedModel().setTransientModel(true);
                } else {
                    super.handleUpdatedModel();

                    if (mainWindow != null)
                        mainWindow.handleUpdatedModel();
                }
            }
            if (needsUpdate && !Thread.holdsLock(updateLock))
                handleUpdatedModel();
        }
    }

    public void undo() {
        TopWorkflowContainer previousModel = getModel().getPreviousCompleted();
        startTransientUpdateMode();
        try {
            previousModel.setAsCurrentVersion();
        } finally {
            endTransientUpdateMode();
        }
        if (previousModel != getModel())
            throw new RuntimeException("Didn't undo properly!");
        deselectAll();
    }

    public void redo() {
        TopWorkflowContainer nextModel = getModel().getNextCompleted();
        startTransientUpdateMode();
        try {
            nextModel.setAsCurrentVersion();
        } finally {
            endTransientUpdateMode();
        }
        if (nextModel != getModel())
            throw new RuntimeException("Didn't redo properly!");
        deselectAll();
    }

    public boolean canRedo() {
        return getModel().getNextCompleted() != null;
    }

    public boolean canUndo() {
        return getModel().getPreviousCompleted() != null;
    }

    @Override
    public TopWorkflowContainer getModel() {
        return (TopWorkflowContainer) super.getModel();
    }

    @Override
    public TopWorkflowContainer getProposedModel() {
        return (TopWorkflowContainer) super.getProposedModel();
    }

    public void handleClick(JavaFXController target, MouseEvent mouseEvent) {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        if (toolbox.getChildren().contains(target)) {
            deselectAll();
            mouseEvent.consume();
            return;
        }

        if (mouseEvent.isMetaDown()) {
            if (target.isSelected()) {
                selected.remove(target);
                target.setSelected(false);
            } else {
                selected.add(target);
                target.setSelected(true);
            }
        } else {
            selected.remove(target);
            deselectAll();
            selected.add(target);
            target.setSelected(true);
        }
        mouseEvent.consume();
    }

    public void deselectAll() {
        for (JavaFXController toDeselect : selected)
            toDeselect.setSelected(false);
        selected.clear();
    }

    public void setSelection(Set<JavaFXController> newSelection) {
        newSelection.remove(this);

        for (JavaFXController toDeselect : selected) {
            if (!newSelection.contains(toDeselect))
                toDeselect.setSelected(false);
        }
        selected.clear();

        for (JavaFXController toSelect : newSelection)
            toSelect.setSelected(true);

        selected.addAll(newSelection);
    }

    public boolean canProcess() {
        return getModel().areChildrenValid() && getModel().isValid();
    }
}

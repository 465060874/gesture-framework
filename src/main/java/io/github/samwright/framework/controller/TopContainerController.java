package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.model.TopWorkflowContainer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Sam Wright Date: 20/08/2013 Time: 18:05
 */
public class TopContainerController extends WorkflowContainerControllerImpl {

    private final Set<ModelController> selected = new HashSet<>();

    private MainWindowController mainWindow;
    private int transientUpdateMode = 0;

    @Getter private EventHandler<KeyEvent> keyPressHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
                Set<ModelController> oldSelected = new HashSet<>(selected);
                deselectAll();
                startTransientUpdateMode();
                try {
                    for (ModelController toDelete : oldSelected) {
                        toDelete.getModel().getCurrentVersion().delete();
                    }
                } finally {
                    endTransientUpdateMode();
                }
            }
        }
    };

    {
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

    public void startTransientUpdateMode() {
//        if (transientUpdateMode)
//            throw new RuntimeException("Already in transient update mode");
        if (transientUpdateMode == 0 && getProposedModel() != null)
            getProposedModel().setTransientUpdate(true);
        ++transientUpdateMode;
    }

    public void endTransientUpdateMode() {
//        if (!transientUpdateMode)
//            throw new RuntimeException("Not in transient update mode, so can't end it");
        --transientUpdateMode;
        if (getProposedModel() != null && transientUpdateMode == 0) {
            getProposedModel().setTransientUpdate(false);
            handleUpdatedModel();
        }
    }

    @Override
    public TopContainerController createClone() {
        return new TopContainerController(this);
    }

    private Object[] updateLock = new Object[0];
    private boolean needsUpdate = false;

    @Override
    public void handleUpdatedModel() {
        try {
            if (getProposedModel() == null)
                return;

            if (Thread.holdsLock(updateLock)) {
                needsUpdate = true;
            } else {
                synchronized (updateLock) {
                    needsUpdate = false;

                    if (transientUpdateMode > 0) {
                        getProposedModel().setTransientUpdate(true);
                    } else {
                        super.handleUpdatedModel();
                        deselectAll();
                        mainWindow.handleUpdatedModel();
                    }
                }
                if (needsUpdate && !Thread.holdsLock(updateLock))
                    handleUpdatedModel();
            }

//            if (!Thread.holdsLock(updateLock) && !needsUpdate && transientUpdateMode != 0)
//                throw new RuntimeException("Transient update mode not 0, was: " + transientUpdateMode);
        } catch (RuntimeException e) {
//            System.out.println("REVERTING!!\n" + e.getStackTrace());
            throw e;
//            revertModel();
        }
    }

    public void undo() {
        TopWorkflowContainer previousModel = getModel().getPrevious();
        startTransientUpdateMode();
        try {
            previousModel.setAsCurrentVersion();
        } finally {
            endTransientUpdateMode();
        }
        if (previousModel != getModel())
            throw new RuntimeException("Didn't undo properly!");

    }

    public void redo() {
        TopWorkflowContainer nextModel = getModel().getNext();
        startTransientUpdateMode();
        try {
            nextModel.setAsCurrentVersion();
        } finally {
            endTransientUpdateMode();
        }
        if (nextModel != getModel())
            throw new RuntimeException("Didn't redo properly!");
    }

    public boolean canRedo() {
        return getModel().getNext() != null;
    }

    public boolean canUndo() {
        return getModel().getPrevious() != null;
    }

    @Override
    public TopWorkflowContainer getModel() {
        return (TopWorkflowContainer) super.getModel();
    }

    @Override
    public TopWorkflowContainer getProposedModel() {
        return (TopWorkflowContainer) super.getProposedModel();
    }

    public void handleClick(ModelController target, MouseEvent mouseEvent) {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        if (toolbox.getChildren().contains(target)) {
            deselectAll();
            mouseEvent.consume();
            return;
        }

        if (mouseEvent.isMetaDown()) {
            if (target.getClickedProperty().get()) {
                selected.remove(target);
                target.getClickedProperty().set(false);
            } else {
                selected.add(target);
                target.getClickedProperty().set(true);
            }
        } else {
            selected.remove(target);
            deselectAll();
            selected.add(target);
            target.getClickedProperty().set(true);
        }
        mouseEvent.consume();
    }

    private void deselectAll() {
        for (ModelController toDeselect : selected)
            toDeselect.getClickedProperty().set(false);
        selected.clear();
    }
}

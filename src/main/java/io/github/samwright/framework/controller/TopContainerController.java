package io.github.samwright.framework.controller;

import io.github.samwright.framework.MainApp;
import io.github.samwright.framework.model.TopWorkflowContainer;
import io.github.samwright.framework.model.common.EventuallyImmutable;
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

    private boolean transientUpdateMode = false;

    @Getter private EventHandler<KeyEvent> keyPressHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            System.out.println("Handling key");
            if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
                System.out.println("Deleting!");
                Set<ModelController> oldSelected = new HashSet<>(selected);
                deselectAll();
                startTransientUpdateMode();
                for (ModelController toDelete : oldSelected) {
                    EventuallyImmutable latest = toDelete.getModel().versionInfo().getLatest();
                    if (!latest.isDeleted())
                        latest.delete();
                }
                endTransientUpdateMode();
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
                System.out.println("Selected: " + selected);
            }
        });
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

    public void startTransientUpdateMode() {
        transientUpdateMode = true;
    }

    public void endTransientUpdateMode() {
        transientUpdateMode = false;
        if (getModel().isTransientUpdate()) {
            getModel().setTransientUpdate(false);
//            handleUpdatedModel();
        }
    }

    @Override
    public TopContainerController createClone() {
        return new TopContainerController(this);
    }

    @Override
    public void handleUpdatedModel() {
        super.handleUpdatedModel();

        if (transientUpdateMode) {
            getModel().setTransientUpdate(true);
        } else {
            super.handleUpdatedModel();
            mainWindow.handleUpdatedModel();
        }

    }

    public void undo() {
        TopWorkflowContainer previousModel = getModel().getPrevious();
        previousModel.setAsCurrentVersion();

        if (previousModel != getModel())
            throw new RuntimeException("Didn't undo properly!");
    }

    public void redo() {
        TopWorkflowContainer nextModel = getModel().getNext();
        nextModel.setAsCurrentVersion();

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

    public void handleClick(ModelController target, MouseEvent mouseEvent) {
        ToolboxController toolbox = (ToolboxController) MainApp.beanFactory.getBean("toolbox");
        if (toolbox.getChildren().contains(target)) {
            deselectAll();
            System.out.println("Selected: " + selected);
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
        System.out.println("Selected: " + selected);
    }

    private void deselectAll() {
        for (ModelController toDeselect : selected)
            toDeselect.getClickedProperty().set(false);
        selected.clear();
    }
}

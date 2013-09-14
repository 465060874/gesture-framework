package io.github.samwright.framework.model.mock;

import io.github.samwright.framework.controller.TopController;
import io.github.samwright.framework.model.WorkflowContainer;

/**
 * The ultimate ancestor of a complete model.
 */
public interface TopProcessor extends WorkflowContainer {

    /**
     * Sets this object as a transient update, meaning it is not an important state.
     * <p/>
     * For example if two elements need to be deleted, the first must be deleted (creating an
     * updated model) then the second must be deleted (creating another updated model).  The
     * interim model is not noteworthy, and is deemed transient.
     * <p/>
     * The controller for this object determines what transience entails, but an obvious example
     * is undo/redo - a transient model should be skipped over.
     *
     * @param transientModel the transience of this model.
     */
    void setTransientModel(boolean transientModel);

    /**
     * Gets the transience of this model.
     * <p/>
     * For example if two elements need to be deleted, the first must be deleted (creating an
     * updated model) then the second must be deleted (creating another updated model).  The
     * interim model is not noteworthy, and is deemed transient.
     * <p/>
     * The controller for this object determines what transience entails, but an obvious example
     * is undo/redo - a transient model should be skipped over.
     *
     * @return the transience of this model.
     */
    boolean isTransientModel();

    /**
     * Gets the previous {@code TopProcessor} that is not transient, or null if none exist.
     *
     * @return the previous non-transient model.
     */
    TopProcessor getPreviousCompleted();

    /**
     * Gets the next {@code TopProcessor} that is not transient, or null if none exist.
     *
     * @return the next non-transient model.
     */
    TopProcessor getNextCompleted();

    @Override
    TopProcessor createMutableClone();

    @Override
    TopController getController();
}

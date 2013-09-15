package io.github.samwright.framework.controller;

import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.helper.Mediator;

import java.util.List;

/**
 * A controller assigned to manage a {@link Processor} object.
 * <p/>
 * When the assigned {@code Processor} is replaced with a newer version, processes data,
 * or is trained, this is notified.
 */
public interface ModelController {
    /**
     * Propose a new model for this to manage.  It might be a new version of the
     * previously-managed {@link Processor}, or it might be a completely new one.
     * <p/>
     * When a model is proposed, it is not immediately accessible by {@code getModel()}, nor is
     * {@code handleUpdatedModel()} immediately called.  The {@link TopController} manages when
     * to call {@code handleUpdatedModel()}, and does so recursively from it down to the lowest
     * {@link Element}.
     *
     * @param proposedModel the {@code Processor} to propose.
     */
    void proposeModel(Processor proposedModel);

    /**
     * Creates a deep clone of this (but with no model).
     *
     * @return a complete clone of this (but with no model).
     */
    ModelController createClone();

    /**
     * Called when a new model has been proposed and the {@link TopController} has determined
     * that the time is right to handle it.
     */
    void handleUpdatedModel();

    /**
     * Gets the currently-managed model.
     * <p/>
     * If a new model is proposed, this method will only return it once
     * {@code handleUpdatedModel()} has been called.  Until then it will return the latest model
     * to be handled by {@code handleUpdatedModel()}.
     *
     * @return the currently-managed model.
     */
    Processor getModel();

    /**
     * Called when the managed {@link Processor} successfully processes input data.
     * <p/>
     * NB. This will be called from the {@code Processor} object's processing thread,
     * which will wait until this method returns, and won't necessarily be the same thread that
     * this object was created with.
     *
     * @param processedData the data the managed {@code Processor} successfully processed.
     */
    void handleProcessedData(Mediator processedData);

    /**
     * Called when the managed {@link Processor} successfully processes a single training datum.
     * <p/>
     * NB. This will be called from the {@code Processor} object's processing thread,
     * which will wait until this method returns, and won't necessarily be the same thread that
     * this object was created with.
     *
     * @param processedTrainingData the data the managed {@code Processor} successfully processed
     *                              from a single input training datum.
     */
    void handleProcessedTrainingData(List<Mediator> processedTrainingData);

    /**
     * Called when the managed {@link Processor} has been successfully trained.
     * <p/>
     * NB. This will be called from the {@code Processor} object's processing thread,
     * which will wait until this method returns, and won't necessarily be the same thread that
     * this object was created with.
     */
    void handleTrained();

}

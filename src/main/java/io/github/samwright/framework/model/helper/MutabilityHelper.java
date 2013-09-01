package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.Replaceable;
import lombok.Getter;
import lombok.NonNull;

/**
 * A helper object that manages mutations in a {@link Processor} object (which delegates to this).
 */
public class MutabilityHelper {
    private static final Object[] writeLock = new Object[0];
    private static MutabilityHelper mutationStarter;

    @Getter private boolean mutable;
    private final Processor managedProcessor;
    @Getter private Processor next, previous;

    /**
     * Construct a new {@code MutabilityHelper} to manage the given {@link Processor}
     * with the given mutability.
     *
     * @param managedProcessor the object for this to manage.
     * @param mutable the initial mutability of the object to manage.
     */
    public MutabilityHelper(@NonNull Processor managedProcessor, boolean mutable) {
        this.mutable = mutable;
        this.managedProcessor = managedProcessor;
    }

    public void replaceWith(@NonNull Replaceable replacement) {
        if (replacement == next)
            return;

        synchronized (writeLock) {
            Processor replacementProcessor = (Processor) replacement;
            checkReplacementValidity(managedProcessor, replacementProcessor);

            startMutation();

            if (getNext() != null)
                managedProcessor.discardNext();

            next = replacementProcessor;
            next.replace(managedProcessor);

            if (pauseMutationIfThisStartedIt())
                getTopModel().afterReplacement();
        }

        endMutationIfPaused();
    }

    public void replace(@NonNull Replaceable toReplace) {
        if (toReplace == previous)
            return;

        synchronized (writeLock) {
            checkReplacementValidity((Processor) toReplace, managedProcessor);

            startMutation();
            previous = (Processor) toReplace;
            toReplace.replaceWith(managedProcessor);

            this.mutable = false;
            if (previous != null) {
                managedProcessor.setController(previous.getController());
                managedProcessor.setUUID(previous.getUUID());
            }

            setAsCurrentVersion();
            pauseMutationIfThisStartedIt();
        }
        endMutationIfPaused();
    }

    private static void checkReplacementValidity(Processor before, Processor after) {
        if (after == before)
            throw new RuntimeException("Tried replacing with self: " + before);
        if (before.isMutable())
            throw new RuntimeException("Cannot replace a mutable object - fix this first");
        if (after.getPrevious() != null && after.getPrevious() != before)
            throw new RuntimeException("Replacement has already replaced something else");
    }

    private void notifyTopController() {
        if (managedProcessor.getController() != null) {
            Processor topModel = getTopModel();
            if (topModel.getController() != null)
                topModel.getController().handleUpdatedModel();
        }
    }

    private Processor getTopModel() {
        Processor parent = managedProcessor;

        while (parent instanceof ChildOf && ((ChildOf) parent).getParent() != null)
            parent = (Processor) ((ChildOf) parent).getParent();

        return parent;
    }

    private void startMutation() {
        if (mutationStarter == null)
            mutationStarter = this;
    }

    private boolean pauseMutationIfThisStartedIt() {
        if (mutationStarter == this) {
            mutationStarter = null;
            return true;
        }
        return false;
    }

    private void endMutationIfPaused() {
        if (!Thread.holdsLock(writeLock)) {
            notifyTopController();
        } else if (mutationStarter == null) {
            mutationStarter = this;
        }
    }

    public void discardNext() {
        synchronized (writeLock) {
            startMutation();
            if (getNext() != null) {
                Processor oldNext = getNext();
                next = null;
                oldNext.discardPrevious();
            }

            pauseMutationIfThisStartedIt();
        }

        endMutationIfPaused();
    }

    public void discardPrevious() {
        synchronized (writeLock) {
            if (getPrevious() != null) {
                startMutation();
                Processor oldPrevious = getPrevious();
                previous = null;
                managedProcessor.setUUID(ModelLoader.makeNewUUID());

                ModelController oldPreviousController = oldPrevious.getController();
                if (oldPreviousController == null)
                    managedProcessor.setController(null);
                else
                    managedProcessor.setController(oldPreviousController.createClone());

                oldPrevious.discardNext();
                pauseMutationIfThisStartedIt();
            }
        }

        endMutationIfPaused();
    }

    public void delete() {
        discardNext();
    }

    public void setAsCurrentVersion() {
        synchronized (writeLock) {
            startMutation();

            if (managedProcessor.getController() != null)
                managedProcessor.getController().proposeModel(managedProcessor);

            ModelLoader.registerProcessor(managedProcessor);
            pauseMutationIfThisStartedIt();
        }
        endMutationIfPaused();
    }

    public Processor getCurrentVersion() {
        return ModelLoader.getProcessor(managedProcessor.getUUID());
    }
}
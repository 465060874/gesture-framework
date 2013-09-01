package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.Replaceable;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

/**
 * A helper object that manages mutations in a {@link Processor} object (which delegates to this).
 */
public class MutabilityHelper {
    private static final Object[] writeLock = new Object[0];
    private static MutabilityHelper mutationStarter;
    private static Reason reason;

    @Getter private boolean mutable;
    @Getter private ModelController controller;
    private final Processor managedProcessor;
    @Getter private Processor next, previous;
    private UUID uuid;


    private static enum Reason {
        BEING_REPLACED, REPLACING, DISCARDING_NEXT, DISCARDING_PREV,
        SETTING_UUID, SETTING_CONTROLLER, SETTING_CURRENT
    }

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

        try {
            synchronized (writeLock) {
                Processor replacementProcessor = (Processor) replacement;
                checkReplacementValidity(managedProcessor, replacementProcessor);

                startMutation(Reason.BEING_REPLACED);

                if (getNext() != null)
                    managedProcessor.discardNext();

                next = replacementProcessor;
                next.replace(managedProcessor);

                if (thisStartedMutation(Reason.BEING_REPLACED))
                    getTopModel().afterReplacement();
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.BEING_REPLACED);
        }
    }

    public void replace(@NonNull Replaceable toReplace) {
        if (toReplace == previous)
            return;

        try {
            synchronized (writeLock) {
                checkReplacementValidity((Processor) toReplace, managedProcessor);

                startMutation(Reason.REPLACING);
                previous = (Processor) toReplace;
                toReplace.replaceWith(managedProcessor);

                this.mutable = false;
                managedProcessor.setController(previous.getController());
                managedProcessor.setUUID(previous.getUUID());

                setAsCurrentVersion();
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.REPLACING);
        }
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

    private void startMutation(Reason forThisReason) {
        if (mutationStarter == null) {
            mutationStarter = this;
            reason = forThisReason;
        }
    }

    private boolean thisStartedMutation(Reason forThisReason) {
        return mutationStarter == this && reason == forThisReason;
    }

    private void endMutationIfThisMethodStartedIt(Reason forThisReason) {
        if (thisStartedMutation(forThisReason) && !Thread.holdsLock(writeLock)) {
            mutationStarter = null;
            reason = null;
            notifyTopController();
        }
    }

    public void discardNext() {
        try {
            synchronized (writeLock) {
                startMutation(Reason.DISCARDING_NEXT);
                if (getNext() != null) {
                    Processor oldNext = getNext();
                    next = null;
                    oldNext.discardPrevious();
                }

                if (thisStartedMutation(Reason.DISCARDING_NEXT))
                    restoreCurrentAfterDiscard(managedProcessor);
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.DISCARDING_NEXT);
        }
    }

    public void discardPrevious() {
        try {
            synchronized (writeLock) {
                if (getPrevious() != null) {
                    startMutation(Reason.DISCARDING_PREV);
                    Processor oldPrevious = getPrevious();
                    previous = null;
                    managedProcessor.setUUID(ModelLoader.makeNewUUID());

                    ModelController oldPreviousController = oldPrevious.getController();
                    if (oldPreviousController == null)
                        managedProcessor.setController(null);
                    else
                        managedProcessor.setController(oldPreviousController.createClone());

                    oldPrevious.discardNext();

                    if (thisStartedMutation(Reason.DISCARDING_PREV))
                        restoreCurrentAfterDiscard(oldPrevious);
                }
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.DISCARDING_PREV);
        }
    }

    private void restoreCurrentAfterDiscard(Processor beforeDiscardPoint) {
        Processor current = beforeDiscardPoint.getCurrentVersion();

        if (current.getUUID().equals(getUUID())) {
            // Current version is after the discard point,
            // so have the old UUID point to oldPrevious as the current...
            beforeDiscardPoint.setAsCurrentVersion();
            // ...and the new UUID point to the new version (after the discard point)
            current.setAsCurrentVersion();
        } else {
            // Current version is before discard point,
            // so set this as the current version for the new UUID...
            setAsCurrentVersion();
            // ...and restore the old UUID's current version as the current one.
            current.setAsCurrentVersion();
        }
    }

    public void delete() {
//        discardNext();
    }

    public void setAsCurrentVersion() {
        try {
            synchronized (writeLock) {
                startMutation(Reason.SETTING_CURRENT);

                if (managedProcessor.getController() != null)
                    managedProcessor.getController().proposeModel(managedProcessor);

                ModelLoader.registerProcessor(managedProcessor);
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.SETTING_CURRENT);
        }
    }

    public void setController(ModelController controller) {
        try {
            synchronized (writeLock) {
                if (this.controller != controller) {
                    startMutation(Reason.SETTING_CONTROLLER);

                    this.controller = controller;
                    if (getNext() != null)
                        getNext().setController(controller);

                    if (thisStartedMutation(Reason.SETTING_CONTROLLER)
                            && controller != null)
                        controller.proposeModel(getCurrentVersion());
                }
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.SETTING_CONTROLLER);
        }
    }


    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        try {
            synchronized (writeLock) {
                if (getUUID() == null || !getUUID().equals(uuid)) {
                    startMutation(Reason.SETTING_UUID);

                    this.uuid = uuid;
                    if (getNext() != null)
                        getNext().setUUID(uuid);

                    if (thisStartedMutation(Reason.SETTING_UUID)) {
                        Processor currentVersion = getCurrentVersion();
                        if (currentVersion == null)
                            managedProcessor.setAsCurrentVersion();
                        else if (currentVersion.getUUID().equals(getUUID()))
                            currentVersion.setAsCurrentVersion();
                    }
                }
            }
        } finally {
            endMutationIfThisMethodStartedIt(Reason.SETTING_UUID);
        }

    }

    public Processor getCurrentVersion() {
        return ModelLoader.getProcessor(managedProcessor.getUUID());
    }
}
package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.controller.MainWindowController;
import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import io.github.samwright.framework.model.common.Replaceable;
import lombok.Getter;
import lombok.NonNull;

/**
 * A helper object that manages an {@link EventuallyImmutable} object (which delegates to this).
 */
public class MutabilityHelper implements EventuallyImmutable {
    private static final Object[] writeLock = new Object[0];

    private VersionInfo versionInfo;
    @Getter private boolean mutable;
    @Getter private boolean deleted;
    @Getter private ModelController controller;

    public static interface ForManualDelegation {

        void fixAsVersion(VersionInfo versionInfo);
        void delete();
        EventuallyImmutable createMutableClone();
        void discardNext();
        void discardPrevious();
    }
    /**
     * Construct a new {@code MutabilityHelper} to manage the given {@link EventuallyImmutable}
     * with the given mutability.
     *
     * @param thisImmutable the object for this to manage.
     * @param mutable the initial mutability of the object to manage.
     */
    public MutabilityHelper(@NonNull EventuallyImmutable thisImmutable, boolean mutable) {
        versionInfo = VersionInfo.createForFirst(thisImmutable);
        this.mutable = mutable;
        deleted = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setController(ModelController controller) {
        if (this.controller != controller) {
            EventuallyImmutable next = versionInfo.getNext();
            this.controller = controller;
            if (next != null) {
                next.setController(controller);
            } else if (controller != null) {
                Replaceable model = versionInfo.getThisVersion();
                controller.setModel(model);
            }
        }
    }

    @Override
    public EventuallyImmutable createMutableClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public VersionInfo versionInfo() {
        synchronized (writeLock) {
            return versionInfo;
        }
    }

    @Override
    public void fixAsVersion(@NonNull VersionInfo versionInfo) {
        synchronized (writeLock) {
            this.versionInfo = versionInfo;
            this.mutable = false;
            if (versionInfo.getPrevious() != null) {
                setController(versionInfo.getPrevious().getController());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void replaceWith(@NonNull EventuallyImmutable replacement) {
        if (versionInfo.getThisVersion() == replacement)
            return;
        System.out.println(" ====== START replacing " + versionInfo.getThisVersion());
        synchronized (writeLock) {
            if (isMutable())
                throw new RuntimeException("Cannot replace a mutable object - fix this first");
            if (isDeleted())
                throw new RuntimeException("Cannot replace a deleted object - discard first.");
            if (versionInfo.getNext() != null)
                throw new RuntimeException("Already been replaced - discard the replacement first");
            if (replacement.versionInfo().getPrevious() != null)
                throw new RuntimeException("Replacement has already replaced something else");

            versionInfo = versionInfo.withNext(replacement);
            VersionInfo nextVersionInfo =
                    replacement.versionInfo().withPrevious(versionInfo.getThisVersion());
            replacement.fixAsVersion(nextVersionInfo);
            replacement.setController(controller);

            if (controller != null)
                controller.setModel(nextVersionInfo.getLatest());

        }

        if (!Thread.holdsLock(writeLock))
            notifyTopController();
    }

    private void notifyTopController() {
        synchronized (writeLock) {
            System.out.println("notifying top controller...");
            MainWindowController.getTopController().handleUpdatedModel();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void discardNext() {
        synchronized (writeLock) {
            if (versionInfo.getNext() != null) {
                EventuallyImmutable oldNext = versionInfo.getNext();
                versionInfo = versionInfo.withNext(null);
                oldNext.discardPrevious();
            }
            deleted = false;

            if (controller != null)
                controller.setModel(versionInfo.getThisVersion());
        }

        if (!Thread.holdsLock(writeLock))
            notifyTopController();
    }

    @Override
    public void discardPrevious() {
        synchronized (writeLock) {
            if (versionInfo.getPrevious() != null) {
                EventuallyImmutable oldPrevious = versionInfo.getPrevious();
                versionInfo = versionInfo.withPrevious(null);
                oldPrevious.discardNext();
            }
        }
    }

    @Override
    public void delete() {
        synchronized (writeLock) {
            if (versionInfo.getNext() != null)
                throw new RuntimeException("Cannot delete - already been replaced.");
            if (isDeleted())
                throw new RuntimeException("Already been deleted.");

            deleted = true;
        }
    }
}
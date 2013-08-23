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
public class MutabilityHelper<T extends EventuallyImmutable> implements EventuallyImmutable {
    private static final Object[] writeLock = new Object[0];

    private VersionInfo<T> versionInfo;
    @Getter private boolean mutable;
    @Getter private boolean deleted;
    @Getter private ModelController<T> controller;

    public static interface ForManualDelegation {

        void fixAsVersion(VersionInfo versionInfo);
        void delete();
        EventuallyImmutable createMutableClone();
        EventuallyImmutable createOrphanedDeepClone();
        void discardNext();
        void discardPrevious();
        void undo();
        void redo();
    }
    /**
     * Construct a new {@code MutabilityHelper} to manage the given {@link EventuallyImmutable}
     * with the given mutability.
     *
     * @param thisImmutable the object for this to manage.
     * @param mutable the initial mutability of the object to manage.
     */
    public MutabilityHelper(@NonNull T thisImmutable, boolean mutable) {
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
                T model = versionInfo.getThisVersion();
                controller.setModel(model);
            }
        }
    }

    @Override
    public T createMutableClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public EventuallyImmutable createOrphanedDeepClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public VersionInfo<T> versionInfo() {
        synchronized (writeLock) {
            return versionInfo;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
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
    public void replaceWith(@NonNull Replaceable replacement) {
        if (versionInfo.getThisVersion() == replacement) {
            System.out.println("SKIPPING REPLACEMENT!!");
            return;
        }

        synchronized (writeLock) {
            if (isMutable())
                throw new RuntimeException("Cannot replace a mutable object - fix this first");
            if (isDeleted())
                throw new RuntimeException("Cannot replace a deleted object - discard first.");
            if (replacement.versionInfo().getPrevious() != null)
                throw new RuntimeException("Replacement has already replaced something else");

            if (versionInfo.getNext() != null)
                versionInfo.getThisVersion().discardNext();

            versionInfo = versionInfo.withNext((T) replacement);
            VersionInfo<T> nextVersionInfo =
                    replacement.versionInfo().withPrevious(versionInfo.getThisVersion());

            ((T) replacement).fixAsVersion(nextVersionInfo);

            if (controller != null)
                controller.setModel((T) replacement);
        }

        if (!Thread.holdsLock(writeLock))
            notifyTopController();
    }

    private void notifyTopController() {
        synchronized (writeLock) {
            if (MainWindowController.getTopController() != null)
                MainWindowController.getTopController().handleUpdatedModel();
        }
    }

    @Override
    public void discardNext() {
        synchronized (writeLock) {
            if (versionInfo.getNext() != null) {
//                System.out.println("Discarding from " + versionInfo.getThisVersion()
//                        + " next " + versionInfo.getNext());
                T oldNext = versionInfo.getNext();
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
                T oldPrevious = versionInfo.getPrevious();
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


    @Override
    public void undo() {
        synchronized (writeLock) {
            if (versionInfo.getPrevious() == null)
                throw new RuntimeException("Cannot undo because there's no previous version");

            if (controller != null)
                controller.setModel(versionInfo.getPrevious());
        }

        if (!Thread.holdsLock(writeLock))
            notifyTopController();
    }

    @Override
    public void redo() {
        synchronized (writeLock) {
            if (versionInfo.getNext() == null)
                throw new RuntimeException("Cannot redo because there's no next version");

            if (controller != null)
                controller.setModel(versionInfo.getNext());
        }

        if (!Thread.holdsLock(writeLock))
            notifyTopController();
    }
}
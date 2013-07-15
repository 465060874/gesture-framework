package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.model.common.EventuallyImmutable;
import lombok.Getter;
import lombok.NonNull;

/**
 * A helper object that manages an {@link EventuallyImmutable} object (which delegates to this).
 */
public class MutabilityHelper implements EventuallyImmutable {
    private VersionInfo versionInfo;
    @Getter private boolean mutable;
    @Getter private boolean deleted;

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
    public EventuallyImmutable createMutableClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public VersionInfo versionInfo() {
        return versionInfo;
    }

    @Override
    public void fixAsVersion(@NonNull VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
        this.mutable = false;
    }

    @Override
    public void replaceWith(@NonNull EventuallyImmutable replacement) {
        if (versionInfo.getThisVersion() == replacement)
            return;
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
    }

    @Override
    public void discardNext() {
        if (versionInfo.getNext() != null) {
            EventuallyImmutable oldNext = versionInfo.getNext();
            versionInfo = versionInfo.withNext(null);
            oldNext.discardPrevious();
        }
        deleted = false;
    }

    @Override
    public void discardPrevious() {
        if (versionInfo.getPrevious() != null) {
            EventuallyImmutable oldPrevious = versionInfo.getPrevious();
            versionInfo = versionInfo.withPrevious(null);
            oldPrevious.discardNext();
        }
    }

    @Override
    public void delete() {
        if (versionInfo.getNext() != null)
            throw new RuntimeException("Cannot delete - already been replaced.");
        if (isDeleted())
            throw new RuntimeException("Already been deleted.");

        deleted = true;
    }
}

package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.EventuallyImmutable;
import BestSoFar.framework.core.common.ParentOf;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A helper object that manages an {@link EventuallyImmutable} object (which delegates to this).
 */
public class MutabilityHelper implements EventuallyImmutable {
    private VersionInfo versionInfo;
    @Getter @Setter private boolean mutable;
    @Getter private boolean deleted;

    public static interface ForManualDelegation {
        void fixAsVersion(VersionInfo versionInfo);
        void delete();
        EventuallyImmutable createMutableClone();
        void discardReplacement();
        void discardOlderVersions();
    }

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
        if (versionInfo.getNext() != null)
            throw new RuntimeException("Already been replaced - discard the replacement first");
        if (replacement.versionInfo().getPrevious() != null)
            replacement.versionInfo().getPrevious().discardReplacement();

        versionInfo = versionInfo.withNext(replacement);
        VersionInfo nextVersionInfo =
                replacement.versionInfo().withPrevious(versionInfo.getThisVersion());
        replacement.fixAsVersion(nextVersionInfo);

    }

    @Override
    public void discardReplacement() {
        versionInfo = versionInfo.withNext(null);
        deleted = false;
    }

    @Override
    public void discardOlderVersions() {
        if (versionInfo.getPrevious() != null) {
            versionInfo.getPrevious().discardReplacement();
            versionInfo = versionInfo.withPrevious(null);
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

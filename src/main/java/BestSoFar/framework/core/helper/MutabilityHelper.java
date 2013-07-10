package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.core.common.EventuallyImmutable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * User: Sam Wright Date: 09/07/2013 Time: 19:52
 */
public class MutabilityHelper implements EventuallyImmutable {
    @Getter private ImmutableVersion version;
    @Getter @Setter private boolean mutable;
    @Getter private boolean deleted;

    public static interface ForManualDelegation {
        void finalise(ImmutableVersion version);
        void delete();
        EventuallyImmutable createMutableClone();
    }

    public MutabilityHelper(@NonNull EventuallyImmutable thisImmutable, boolean mutable) {
        version = new ImmutableVersion(thisImmutable);
        this.mutable = mutable;
        deleted = false;
    }

    @Override
    public EventuallyImmutable createMutableClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void finalise(@NonNull ImmutableVersion version) {
        this.version = version;
        this.mutable = false;
    }

    @Override
    public void replaceWith(@NonNull EventuallyImmutable replacement) {
        if (version.getImmutable() == replacement)
            return;
        if (isMutable())
            throw new RuntimeException("Cannot replace a mutable object - finalise this first");
        if (version.getNext() != null)
            throw new RuntimeException("Already been replaced - discard the replacement first");
        if (replacement.getVersion().getPrevious() != null)
            replacement.getVersion().getPrevious().discardReplacement();

        version = version.withNext(replacement);
        ImmutableVersion nextVersion = replacement.getVersion().withPrevious(version.getImmutable());
        replacement.finalise(nextVersion);

    }

    @Override
    public void discardReplacement() {
        version = version.withNext(null);
        deleted = false;
    }

    @Override
    public void delete() {
        if (version.getNext() != null)
            throw new RuntimeException("Cannot delete - already been replaced.");
        if (isDeleted())
            throw new RuntimeException("Already been deleted.");

        deleted = true;
    }
}

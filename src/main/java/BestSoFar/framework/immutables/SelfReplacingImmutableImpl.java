package BestSoFar.framework.immutables;

import BestSoFar.framework.core.AbstractElement;
import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.immutables.common.HandledImmutable;
import BestSoFar.framework.immutables.common.Immutable;
import BestSoFar.framework.immutables.common.SelfReplacingImmutable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User: Sam Wright Date: 26/06/2013 Time: 19:22
 */
public class SelfReplacingImmutableImpl implements SelfReplacingImmutable, Deletable {
    @Getter private SelfReplacingImmutable replacement, replaced;
    @Getter private boolean mutable, deleted = false;
    @Getter private int age;

    public static interface ToOverride {

        void delete();
        void finalise();
        HandledImmutable createClone(boolean mutable);
        SelfReplacingImmutableImpl getReplacement();

        SelfReplacingImmutableImpl getReplaced();
    }

    public SelfReplacingImmutableImpl(boolean mutable) {
        this.mutable = mutable;
        age = 0;
    }

    @Override
    final public boolean hasReplacement() {
        return replacement != null;
    }

    @Override
    final public boolean hasReplaced() {
        return replaced != null;
    }

    @Override
    final public void discardReplacement() {
        replacement = null;
        deleted = false;
    }

    @Override
    final public void discardReplaced() {
        replaced = null;
    }

    @Override
    final public SelfReplacingImmutable getLatest() {
        SelfReplacingImmutable pointer = this;
        while (pointer.hasReplacement())
            pointer = pointer.getReplacement();

        return pointer;
    }

    @Override
    final public SelfReplacingImmutable getEarliest() {
        SelfReplacingImmutable pointer = this;
        while (pointer.hasReplaced())
            pointer = pointer.getReplaced();

        return pointer;
    }

    @Override
    public HandledImmutable createClone(boolean mutable) {
        throw new UnsupportedOperationException("To be overriden");
    }

    @Override
    public void finalise() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public void proposeReplacement(Immutable proposed) {
        if (hasReplacement())
            throw new RuntimeException("Replacement already exists");
        if (isMutable())
            throw new RuntimeException("Cannot replace mutable object.  Must finalise it first.");
        if (isDeleted())
            throw new RuntimeException("Cannot replace deleted object (deletion is a form of " +
                    "mutation). Perform discardReplacement() first.");

        SelfReplacingImmutableImpl proposedReplacement = (SelfReplacingImmutableImpl) proposed;

        if (proposedReplacement.hasReplaced())
            throw new RuntimeException("Proposed replacement already replaces another.");
        if (proposedReplacement.hasReplacement())
            throw new RuntimeException("Proposed replacement has already been replaced");

        assert proposedReplacement.isMutable();

        proposedReplacement.replaced = this;
        proposedReplacement.age = age + 1;
        replacement = proposedReplacement;
        replacement.finalise();
        mutable = false;
    }

    @Override
    final public void delete() {
        deleted = true;
    }
}

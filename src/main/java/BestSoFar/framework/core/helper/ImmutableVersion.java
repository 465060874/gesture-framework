package BestSoFar.framework.core.helper;

import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.core.common.EventuallyImmutable;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * User: Sam Wright Date: 09/07/2013 Time: 13:05
 */
public class ImmutableVersion {
    @Getter private EventuallyImmutable next, previous;
    @Getter private int age;
    @Getter final private EventuallyImmutable immutable;

    public ImmutableVersion(EventuallyImmutable immutable) {
        this.immutable = immutable;
        age = 0;
    }

    private ImmutableVersion(ImmutableVersion toClone) {
        this.previous = toClone.previous;
        this.next = toClone.next;
        this.age = toClone.age;
        this.immutable = toClone.immutable;
    }

    public ImmutableVersion withNext(EventuallyImmutable replacement) {
        ImmutableVersion clone = new ImmutableVersion(this);
        clone.next = replacement;
        return clone;
    }

    public ImmutableVersion withPrevious(EventuallyImmutable replaced) {
        ImmutableVersion clone = new ImmutableVersion(this);
        clone.previous = replaced;
        clone.age = age + 1;
        return clone;
    }

    @SuppressWarnings("unchecked")
    public EventuallyImmutable getLatest() {
        EventuallyImmutable next, pointer = immutable;
        while (null != (next = pointer.getVersion().getNext()))
            pointer = next;

        return pointer;
    }

    @SuppressWarnings("unchecked")
    public EventuallyImmutable getEarliest() {
        EventuallyImmutable previous, pointer = immutable;
        while (null != (previous = pointer.getVersion().getPrevious()))
            pointer = previous;

        return pointer;
    }

    @SuppressWarnings("unchecked")
    public static <E> void updateAllToLatest(Collection<E> oldVersions) {
        List<E> newVersions = new LinkedList<>();

        for (E oldVersion : oldVersions) {
            if (oldVersion instanceof EventuallyImmutable) {
                EventuallyImmutable newVersion = (EventuallyImmutable) oldVersion;
                newVersion = newVersion.getVersion().getLatest();
                if (!newVersion.isDeleted())
                    newVersions.add((E) newVersion);
            } else {
                newVersions.add(oldVersion);
            }
        }

        oldVersions.clear();
        oldVersions.addAll(newVersions);
    }
}

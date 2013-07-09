package BestSoFar.framework.immutables;

import BestSoFar.framework.core.common.Deletable;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
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
    final private EventuallyImmutable thisImmutable;

    public ImmutableVersion(EventuallyImmutable thisImmutable) {
        this.thisImmutable = thisImmutable;
        age = 0;
    }

    private ImmutableVersion(ImmutableVersion toClone) {
        this.previous = toClone.previous;
        this.next = toClone.next;
        this.age = toClone.age;
        this.thisImmutable = toClone.thisImmutable;
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
        EventuallyImmutable next, pointer = thisImmutable;
        while (null != (next = pointer.getVersion().getNext()))
            pointer = next;

        return pointer;
    }

    @SuppressWarnings("unchecked")
    public EventuallyImmutable getEarliest() {
        EventuallyImmutable previous, pointer = thisImmutable;
        while (null != (previous = pointer.getVersion().getPrevious()))
            pointer = previous;

        return pointer;
    }

    @SuppressWarnings("unchecked")
    public static <E> void updateAllToLatest(Collection<E> oldVersions) {
        List<E> newVersions = new LinkedList<>();

        for (E oldVersion : oldVersions) {
            E newVersion = oldVersion;

            if (oldVersion instanceof EventuallyImmutable)
                newVersion = (E) ((EventuallyImmutable) oldVersion).getVersion().getLatest();

            if (newVersion instanceof Deletable) {
                if (! ((Deletable) newVersion).isDeleted() )
                    newVersions.add(newVersion);
            } else {
                newVersions.add(newVersion);
            }
        }

        oldVersions.clear();
        oldVersions.addAll(newVersions);
    }
}

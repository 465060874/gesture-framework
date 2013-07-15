package BestSoFar.framework.core.helper;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * A completed training batch - containing the set of all output mediators and the subset of
 * those which went on to be successful.
 */
public class CompletedTrainingBatch<O> {
    @Getter private final Set<Mediator<O>> all, successful;

    public CompletedTrainingBatch(Set<Mediator<O>> all, Set<Mediator<O>> successful) {
        this.all = Collections.unmodifiableSet(all);
        this.successful = Collections.unmodifiableSet(successful);

        if (!this.all.containsAll(this.successful))
            throw new RuntimeException("'Successful' set must be subset of 'all' set");
    }

}

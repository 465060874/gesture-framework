package io.github.samwright.framework.model.helper;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * A completed training batch - containing the set of all output mediators and the subset of
 * those which went on to be successful.
 */
public class CompletedTrainingBatch {
    @Getter private final Set<Mediator> all, successful;

    public CompletedTrainingBatch(Set<Mediator> all, Set<Mediator> successful) {
        this.all = Collections.unmodifiableSet(all);
        this.successful = Collections.unmodifiableSet(successful);

        if (!this.all.containsAll(this.successful))
            throw new RuntimeException("'Successful' set must be subset of 'all' set");
    }

    public CompletedTrainingBatch rollBack() {
        Set<Mediator> allInputs = Mediator.rollbackMediators(all);
        Set<Mediator> successfulInputs = Mediator.rollbackMediators(successful);

        return new CompletedTrainingBatch(allInputs, successfulInputs);
    }

}

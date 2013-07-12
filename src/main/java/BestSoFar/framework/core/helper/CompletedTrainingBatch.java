package BestSoFar.framework.core.helper;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * User: Sam Wright Date: 11/07/2013 Time: 23:33
 */
public class CompletedTrainingBatch<O> {
    @Getter private final Set<Mediator<O>> all, successful;

    public CompletedTrainingBatch(Set<Mediator<O>> all, Set<Mediator<O>> successful) {
        this.all = Collections.unmodifiableSet(all);
        this.successful = Collections.unmodifiableSet(successful);
    }

}

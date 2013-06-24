package attempt2;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Sam Wright
 * Date: 24/06/2013
 * Time: 10:36
 */
public interface Trainable<I, O> {
    /**
     * After the training batch has been processed to completion, this method is
     * called to allow this object to train.  This method must ensure that each
     * List<Mediator> in the map contains only one mediator (that , and it gives this Processor the chance to train.
     *
     * @param mappings
     */
    void condenseCompletedTrainingBatch(Map<Mediator<I>, List<Mediator<O>>> mappings);
}

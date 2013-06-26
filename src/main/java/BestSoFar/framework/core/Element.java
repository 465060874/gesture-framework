package BestSoFar.framework.core;

/**
 * An elemental Processor is one which always has a one-to-one mapping of input
 * data to output data (through the 'process' and 'processTrainingBatch' method).
 *
 * Every element sits inside a Workflow (ie. its parent).
 *
 * The parent field is always up to date - eg. if the parent is replaced (because
 * it was modified) then this object's parent field is updated (even though data
 * returned by the 'process' method will still flow back to the original parent).
 */
public interface Element<I, O> extends Processor<I, O> {

    /**
     * Gets the workflow in which this element resides.
     *
     * @return the workflow in which this element resides.
     */
    Workflow<?, ?> getParent();

    /**
     * Sets the workflow in which this element resides.
     *
     * @param parent the workflow in which this element resides.
     */
    void setParent(Workflow<?, ?> parent);
}

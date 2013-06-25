package BestSoFar.framework.abstractions;

/**
 * An elemental Processor is one which always has a one-to-one mapping of input
 * data to output data (through the 'process' and 'processTrainingBatch' method).
 *
 * This
 */
public interface Element<I,O> extends Processor<I, O> {

    /**
     * Gets the workflow in which this element resides.
     *
     * The parent field is always up to date - eg. if the parent is replaced, this returns
     * the new parent.  The 'process' flow of data already in the system is unchanged (ie.
     * still flows back to the old parent) since everything is immutable.
     *
     * @return the workflow in which this element resides.
     */
    Workflow<?, ?> getParent();

    void setParent(Workflow<?, ?> parent);

}

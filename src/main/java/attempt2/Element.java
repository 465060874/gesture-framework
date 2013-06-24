package attempt2;

import javax.swing.*;

/**
 * An elemental Processor is one which always has a one-to-one mapping of input
 * data to output data (through the 'process' and 'processTrainingBatch' method).
 *
 * This
 */
public interface Element<I,O> extends Processor<I, O>, SingleParentChild<Workflow<?, ?>> {

}

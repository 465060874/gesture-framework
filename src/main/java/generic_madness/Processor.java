package generic_madness;

import attempt1.DataType;
import attempt1.Mediator;

/**
 * User: Sam Wright
 * Date: 20/06/2013
 * Time: 05:48
 */
public interface Processor<
        I extends Mediator<? extends DataType>,
        O extends Mediator<? extends DataType>
        > {

    /**
     * Returns whether the processor is able to process data.
     *
     * @return whether the processor is able to process data.
     */
    boolean isValid();


    /**
     * Process the input data and return the output data.
     *
     * @param m the mediator object containing
     * @return
     */
    O process(I m);



}


class Data1 implements DataType {}

class Data2 implements DataType {}

class Med1 extends Mediator<Data1> {}

class Med2 extends Mediator<Data2> {}

class ProA implements Processor<Med1,Med2> {

    @Override
    public boolean isValid() {
        return false; // Dummy implementation
    }

    @Override
    public Med2 process(Med1 m) {
        return null; // Dummy implementation
    }
}


class ProB implements Processor<Med2,Med1> {

    @Override
    public boolean isValid() {
        return false; // Dummy implementation
    }

    @Override
    public Med1 process(Med2 m) {
        return null; // Dummy implementation
    }

}



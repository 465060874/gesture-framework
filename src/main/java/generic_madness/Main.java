package generic_madness;

import attempt1.DataType;
import attempt1.Element;
import attempt1.Mediator;

import java.util.Collections;
import java.util.List;

/**
 * User: Sam Wright
 * Date: 20/06/2013
 * Time: 14:05
 */
public class Main {
    //    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        // Check that ProA can come before ProB
        Processor<? extends Mediator<? extends DataType>,
                ? extends Mediator<? extends DataType>> a, b;
        a = new ProA();
        b = new ProB();

        assert Processor.class.isAssignableFrom(a.getClass());
        assert Processor.class.isAssignableFrom(ProA.class);

        System.out.println("Should work:");
        a.getClass().cast(a).process(new Med1());

        System.out.println("Won't work:");
//        a.getClass().cast(a).process(new Med2());


        // Inspect ProB to get input=Med2.class, and output=Med1.class
        //b.getClass().getDeclaredMethod()

        new TypedProcessor(Med1.class, Med2.class);

        TypedProcessor ta = TypedProcessor.newInstance(Med1.class, Med2.class);
        TypedProcessor tb = TypedProcessor.newInstance(Med2.class, Med1.class);

        System.out.println("Med1 -> Med2 : " + checkValid(ta,tb));
        System.out.println("Med2 -> Med2 : " + checkValid(tb, tb));
        System.out.println("Med1 -> Med1 : " + checkValid(ta, ta));
        System.out.println("Med2 -> Med1 : " + checkValid(tb, ta));

    }

    private static boolean checkValid(TypedProcessor before, TypedProcessor after) {
        return after.getInputType().isAssignableFrom(before.getOutputType());
    }
}

class TypedProcessor<
        I extends Mediator<? extends DataType>,
        O extends Mediator<? extends DataType>
        > {

    private final Class<I> inputType;
    private final Class<O> outputType;

    public TypedProcessor(Class<I> inputType, Class<O> outputType) {
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public static <
            I extends Mediator<? extends DataType>,
            O extends Mediator<? extends DataType>>
    TypedProcessor<I,O> newInstance(Class<I> inputType, Class<O> outputType) {
        return new TypedProcessor<I, O>(inputType, outputType);
    }

    public void isInputValid(I obj) {}

    public void isOutputValid(O obj) {}

    Class<I> getInputType() {List<Element> t;
        return inputType;
    }

    Class<O> getOutputType() {
        return outputType;
    }
}


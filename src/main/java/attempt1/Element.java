package attempt1;

/**
 * User: Sam Wright
 * Date: 21/06/2013
 * Time: 02:49
 */
public interface Element<I extends DataType, O extends DataType>
        extends Processor<I,O> {

}


class GenClass<T> {
    private Class<T> cls;

    public GenClass(Class<T> cls) {
        this.cls = cls;
    }

    public Class<T> getCls() {
        return cls;
    }

    public static void main(String[] args) {
        new GenClass<>(Integer.class).getCls();

    }

}

class A {
}

class B extends A {
}

interface I {
    A foo();
}

class C implements I {
    public B foo() {
        return null;
    }

    public static void main(String[] args) {
        C c = new C();
        c.foo(); // return type is B

        I i = new C();
        i.foo(); // return type is A
    }
}
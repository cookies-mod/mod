package codes.cookies.mod.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Int reference to provide and modify a fiel in or something equal, without having the direct reference to that.
 */
@SuppressWarnings("MissingJavadoc")
public class IntReference {
    private final Supplier<Integer> supplier;
    private final Consumer<Integer> consumer;

    public IntReference(Supplier<Integer> supplier, Consumer<Integer> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    public int get() {
        return supplier.get();
    }

    public void set(int value) {
        consumer.accept(value);
    }
}

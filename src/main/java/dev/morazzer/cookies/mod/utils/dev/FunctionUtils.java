package dev.morazzer.cookies.mod.utils.dev;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import java.util.function.Supplier;

import org.apache.logging.log4j.util.TriConsumer;

/**
 * Helper methods to extract/modify certain parts of functions.
 */
public class FunctionUtils {

    public static <O, T> Function<T, Consumer<O>> function(BiConsumer<T, O> consumer) {
        return t -> o -> consumer.accept(t, o);
    }

    public static <O, T1, T2> Function<O, BiConsumer<T1, T2>> function(TriConsumer<O, T1, T2> consumer) {
        return o -> (t1, t2) -> consumer.accept(o, t1, t2);
    }

    public static <O, T1, T2, T3> Function<O, TriConsumer<T1, T2, T3>> function(QuadConsumer<O, T1, T2, T3> consumer) {
        return o -> (t1, t2, t3) -> consumer.accept(o, t1, t2, t3);
    }

    public static <O, T1, T2, T3, T4> Function<O, QuadConsumer<T1, T2, T3, T4>> function(PentaConsumer<O, T1, T2, T3, T4> consumer) {
        return o -> (t1, t2, t3, t4) -> consumer.accept(o, t1, t2, t3, t4);
    }

	public static <T, V> Function<T, Optional<V>> wrapOptionalF(Function<T, V> f) {
		return t -> Optional.ofNullable(f.apply(t));
	}

	public static <T> Supplier<Optional<T>> wrapOptionalSupplier(Supplier<T> supplier) {
		return () -> Optional.ofNullable(supplier.get());
	}

	public interface QuadConsumer<T1, T2, T3, T4> {
        void accept(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    public interface PentaConsumer<T1, T2, T3, T4, T5> {
        void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }

    public static <T> Consumer<T> noOp() {
        return t -> {};
    }

    public static <T1, T2> BiConsumer<T1, T2> noOp2() {
        return (t1, t2) -> {};
    }

    public static <T1, T2, T3> TriConsumer<T1, T2, T3> noOp3() {
        return (t1, t2, t3) -> {};
    }

    public static <T1, T2, T3, T4> QuadConsumer<T1, T2, T3, T4> noOp4() {
        return (t1, t2, t3, t4) -> {};
    }


}

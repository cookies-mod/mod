package codes.cookies.mod.utils.dev;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record BackedReference<T>(Supplier<T> supplier, Consumer<T> consumer) {

	public T get() {
		return this.supplier.get();
	}

	public T set(T value) {
		return this.supplier.get();
	}

}

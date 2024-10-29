package dev.morazzer.cookies.mod.utils;

import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Result<T, E> {


	static <T, E> Result<T, E> success(@NotNull T value) {
		return new Success<>(value);
	}

	static <T, E> Result<T, E> error(@NotNull E error) {
		return new Error<>(error);
	}

	Optional<T> getResult();

	Optional<E> getError();

	default boolean isSuccess() {
		return this instanceof Result.Success<T, E>;
	}

	default boolean isError() {
		return this instanceof Result.Error<T, E>;
	}

	default Result<T, E> ifSuccess(Consumer<T> consumer) {
		if (this instanceof Result.Success<T, E> success) {
			consumer.accept(success.type());
		}
		return this;
	}

	default Result<T, E> ifError(Consumer<E> consumer) {
		if (this instanceof Result.Error<T, E> error) {
			consumer.accept(error.error());
		}
		return this;
	}

	@Nullable
	default T unbox() {
		return getResult().orElse(null);
	}

	@NotNull
	default T unwrap() {
		return getResult().orElseThrow(NullPointerException::new);
	}

	record Success<T, E>(@NotNull T type) implements Result<T, E> {
		@Override
		public Optional<T> getResult() {
			return Optional.of(type);
		}

		@Override
		public Optional<E> getError() {
			return Optional.empty();
		}
	}

	record Error<T, E>(@NotNull E error) implements Result<T, E> {
		@Override
		public Optional<T> getResult() {
			return Optional.empty();
		}

		@Override
		public Optional<E> getError() {
			return Optional.of(error);
		}
	}
}

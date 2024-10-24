package dev.morazzer.cookies.mod.utils;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public interface Result<T, E> {

	static <T, E> Result<T, E> success(T value) {
		return new Success<>(value);
	}

	static <T, E> Result<T, E> error(E error) {
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

	record Success<T, E>(T type) implements Result<T, E> {
		@Override
		public Optional<T> getResult() {
			return Optional.ofNullable(type);
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

package codes.cookies.mod.utils;

import java.util.Optional;

/**
 * Utility to represent a return value that can be either on or another thing.
 * @param <A> First type.
 * @param <B> Second type.
 */
public interface Either<A, B> {

	/**
	 * @return Whether it's the left element.
	 */
	boolean isLeft();

	/**
	 * @return Whether it's the right element.
	 */
	boolean isRight();

	/**
	 * @return The left element.
	 */
	Optional<A> getLeft();

	/**
	 * @return The right element.
	 */
	Optional<B> getRight();

	/**
	 * Creates an instance with the left element being present.
	 * @param a The value.
	 * @return The instance.
	 * @param <A> The left type.
	 * @param <B> The right type.
	 */
	static <A, B> Either<A, B> left(A a) {
		return new Left<>(a);
	}

	/**
	 * Creates an instance with the right element being present.
	 * @param b The value.
	 * @return The instance.
	 * @param <A> The left type.
	 * @param <B> The right type.
	 */
	static <A, B> Either<A, B> right(B b) {
		return new Right<>(b);
	}

	/**
	 * Implementation for the right side.
	 * @param b The value.
	 * @param <A> The left type.
	 * @param <B> The right type.
	 */
	record Right<A,B>(B b) implements Either<A, B> {

		@Override
		public boolean isLeft() {
			return false;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		@Override
		public Optional<A> getLeft() {
			return Optional.empty();
		}

		@Override
		public Optional<B> getRight() {
			return Optional.ofNullable(b);
		}
	}

	/**
	 * Implementation for the left side.
	 * @param a The value.
	 * @param <A> The left type.
	 * @param <B> The right type.
	 */
	record Left<A, B>(A a) implements Either<A, B> {
		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public boolean isRight() {
			return false;
		}

		@Override
		public Optional<A> getLeft() {
			return Optional.ofNullable(a);
		}

		@Override
		public Optional<B> getRight() {
			return Optional.empty();
		}

	}
}

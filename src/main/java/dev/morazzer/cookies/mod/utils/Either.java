package dev.morazzer.cookies.mod.utils;

import java.util.Optional;

public interface Either<A, B> {

	boolean isLeft();
	boolean isRight();
	Optional<A> getLeft();
	Optional<B> getRight();

	static <A, B> Either<A, B> left(A a) {
		return new Left<>(a);
	}

	static <A, B> Either<A, B> right(B b) {
		return new Right<>(b);
	}

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

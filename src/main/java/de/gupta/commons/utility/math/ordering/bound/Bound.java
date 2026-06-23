package de.gupta.commons.utility.math.ordering.bound;

public sealed interface Bound<E> permits Bound.Closed, Bound.Open
{
	E value();

	default boolean isOpen()
	{
		return !isClosed();
	}

	boolean isClosed();

	Bound<E> closure();

	record Closed<E>(E value) implements Bound<E>
	{
		static <E> Closed<E> of(E value)
		{
			return new Closed<>(value);
		}

		@Override
		public boolean isClosed()
		{
			return true;
		}

		@Override
		public Bound<E> closure()
		{
			return this;
		}
	}

	record Open<E>(E value) implements Bound<E>
	{
		@Override
		public boolean isClosed()
		{
			return false;
		}

		@Override
		public Bound<E> closure()
		{
			return Closed.of(value);
		}
	}
}
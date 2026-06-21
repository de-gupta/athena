package de.gupta.commons.utility.math.ordering.element.interval.bound;

public sealed interface Bound<E> permits Bound.Closed, Bound.Open
{
	E value();

	default boolean isOpen()
	{
		return !isClosed();
	}

	boolean isClosed();

	record Closed<E>(E value) implements Bound<E>
	{
		@Override
		public boolean isClosed()
		{
			return true;
		}
	}

	record Open<E>(E value) implements Bound<E>
	{
		@Override
		public boolean isClosed()
		{
			return false;
		}
	}
}
package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

record BoundedIntervalImpl<E>(Bound<E> lower, Bound<E> upper) implements BoundedInterval<E>
{
	static <E> BoundedIntervalImpl<E> of(final Bound<E> lower, final Bound<E> upper)
	{
		return new BoundedIntervalImpl<>(lower, upper);
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return Optional.of(lower);
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return Optional.of(upper);
	}
}

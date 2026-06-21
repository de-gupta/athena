package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

record UnboundedIntervalImpl<E>(Optional<Bound<E>> lower, Optional<Bound<E>> upper) implements UnboundedInterval<E>
{
	static <E> UnboundedInterval<E> withLowerBound(final Bound<E> lower)
	{
		return of(Optional.of(lower), Optional.empty());
	}

	static <E> UnboundedInterval<E> of(final Optional<Bound<E>> lower, final Optional<Bound<E>> upper)
	{
		return new UnboundedIntervalImpl<>(lower, upper);
	}

	static <E> UnboundedInterval<E> withUpperBound(final Bound<E> upper)
	{
		return of(Optional.empty(), Optional.of(upper));
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return lower;
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return upper;
	}

	UnboundedIntervalImpl
	{
		if (lower.isPresent() && upper.isPresent())
			throw new IllegalArgumentException("Use BoundedInterval when both bounds are present.");
	}
}
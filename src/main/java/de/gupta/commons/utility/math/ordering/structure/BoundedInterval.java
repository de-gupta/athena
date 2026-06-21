package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

import java.util.Optional;

public record BoundedInterval<E>(Bound<E> lower, Bound<E> upper) implements Interval<E>
{
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
package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

import java.util.Optional;

public record UnboundedInterval<E>(Optional<Bound<E>> lower, Optional<Bound<E>> upper) implements Interval<E>
{
	public UnboundedInterval
	{
		if (lower.isPresent() && upper.isPresent())
			throw new IllegalArgumentException("Use BoundedInterval when both bounds are present.");
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
}
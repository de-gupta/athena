package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public sealed interface Interval<E> permits BoundedInterval, UnboundedInterval
{
	default boolean isUnbounded()
	{
		return lowerBound().isEmpty() || upperBound().isEmpty();
	}

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();
}
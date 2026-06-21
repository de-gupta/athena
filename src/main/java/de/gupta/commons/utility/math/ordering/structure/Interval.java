package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public sealed interface Interval<E> permits EmptyInterval, BoundedInterval
{
	boolean isEmpty();

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();
}

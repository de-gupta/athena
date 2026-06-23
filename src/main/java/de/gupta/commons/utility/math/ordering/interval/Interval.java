package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;

import java.util.Optional;

public sealed interface Interval<E> permits BoundedInterval, UnboundedInterval
{
	boolean contains(E element);

	boolean contains(Interval<E> other);

	boolean overlaps(Interval<E> other);

	boolean abuts(Interval<E> other);

	Interval<E> span(Interval<E> other);

	Optional<BoundedInterval<E>> intersect(BoundedInterval<E> other);

	Optional<Interval<E>> intersect(UnboundedInterval<E> other);

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();
}

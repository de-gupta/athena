package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;

import java.util.Optional;

public sealed interface Interval<E> permits BoundedInterval, UnboundedInterval
{
	boolean contains(final E element);

	boolean contains(final Interval<E> other);

	boolean overlaps(final Interval<E> other);

	boolean abuts(final Interval<E> other);

	Interval<E> span(final Interval<E> other);

	Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other);

	Optional<Interval<E>> intersect(final Interval<E> other);

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();

	Interval<E> closure();
}
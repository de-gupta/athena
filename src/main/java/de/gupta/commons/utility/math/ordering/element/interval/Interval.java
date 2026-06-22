package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

import java.util.Optional;

public sealed interface Interval<E extends TotallyOrdered<E>> permits BoundedInterval, UnboundedInterval
{
	boolean contains(final E element);

	boolean overlaps(final Interval<E> other);

	boolean abuts(final Interval<E> other);

	Interval<E> span(final Interval<E> other);

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();
}
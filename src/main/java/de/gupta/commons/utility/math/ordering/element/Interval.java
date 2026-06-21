package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public sealed interface Interval<E extends TotallyOrdered<E>> permits EmptyInterval, BoundedInterval
{
	boolean isEmpty();

	boolean isPoint();

	boolean contains(E element);

	boolean contains(Interval<E> other);

	boolean overlaps(Interval<E> other);

	Interval<E> intersect(Interval<E> other);

	boolean abuts(Interval<E> other);

	Optional<Bound<E>> lowerBound();

	Optional<Bound<E>> upperBound();
}

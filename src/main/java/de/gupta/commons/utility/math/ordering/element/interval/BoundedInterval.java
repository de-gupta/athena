package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

import java.util.Optional;

public sealed interface BoundedInterval<E extends TotallyOrdered<E>> extends Interval<E> permits BoundedIntervalImpl
{
	Bound<E> lower();

	Bound<E> upper();

	boolean isPoint();

	Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other);
}
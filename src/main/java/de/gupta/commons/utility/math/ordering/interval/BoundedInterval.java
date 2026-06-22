package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;

import java.util.Optional;

public sealed interface BoundedInterval<E> extends Interval<E> permits BoundedIntervalImpl
{
	Bound<E> lower();

	Bound<E> upper();

	boolean isPoint();

	Optional<BoundedInterval<E>> intersect(BoundedInterval<E> other);

	Optional<BoundedInterval<E>> intersect(UnboundedInterval<E> other);
}

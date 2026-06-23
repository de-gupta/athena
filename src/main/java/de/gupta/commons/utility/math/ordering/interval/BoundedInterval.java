package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;

public sealed interface BoundedInterval<E> extends Interval<E> permits BoundedIntervalImpl
{
	Bound<E> lower();

	Bound<E> upper();

	boolean isPoint();

}
package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

public sealed interface BoundedInterval<E> extends Interval<E> permits BoundedIntervalImpl
{
	Bound<E> lower();

	Bound<E> upper();
}
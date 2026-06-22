package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

public sealed interface BoundedInterval<E extends TotallyOrdered<E>> extends Interval<E> permits BoundedIntervalImpl
{
	Bound<E> lower();

	Bound<E> upper();
}
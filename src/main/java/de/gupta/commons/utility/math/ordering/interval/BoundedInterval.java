package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;

public sealed interface BoundedInterval<E> extends Interval<E> permits BoundedIntervalImpl
{
	boolean isPoint();

	default E lowerValue()
	{
		return lower().value();
	}

	Bound<E> lower();

	default E upperValue()
	{
		return upper().value();
	}

	Bound<E> upper();
}
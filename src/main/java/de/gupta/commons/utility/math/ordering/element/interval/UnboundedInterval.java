package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

import java.util.Optional;

public sealed interface UnboundedInterval<E extends TotallyOrdered<E>> extends Interval<E> permits UnboundedIntervalImpl
{
	default boolean isBoundedBelow()
	{
		return lower().isPresent();
	}

	Optional<Bound<E>> lower();

	default boolean isBoundedAbove()
	{
		return upper().isPresent();
	}

	Optional<Bound<E>> upper();
}
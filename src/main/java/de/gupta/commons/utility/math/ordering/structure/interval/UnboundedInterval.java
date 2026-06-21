package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public sealed interface UnboundedInterval<E> extends Interval<E> permits UnboundedIntervalImpl
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
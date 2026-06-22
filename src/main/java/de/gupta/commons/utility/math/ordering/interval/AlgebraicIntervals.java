package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.ordering.bound.Bound;

public final class AlgebraicIntervals
{
	public static <E extends OrderedAdditiveGroup<E>> E length(final BoundedInterval<E> interval)
	{
		return interval.upper().value().subtract(interval.lower().value());
	}

	public static <E extends OrderedAdditiveGroup<E>> Interval<E> shift(final Interval<E> interval, final E delta)
	{
		return switch (interval)
		{
			case BoundedInterval<E> b -> shift(b, delta);
			case UnboundedInterval<E> u -> shift(u, delta);
		};
	}

	public static <E extends OrderedAdditiveGroup<E>> BoundedInterval<E> shift(final BoundedInterval<E> interval,
	                                                                           final E delta)
	{
		return ((BoundedIntervalImpl<E>) interval).withBounds(
				shiftBound(interval.lower(), delta), shiftBound(interval.upper(), delta));
	}

	public static <E extends OrderedAdditiveGroup<E>> UnboundedInterval<E> shift(final UnboundedInterval<E> interval,
	                                                                             final E delta)
	{
		return ((UnboundedIntervalImpl<E>) interval).withBounds(
				interval.lower().map(b -> shiftBound(b, delta)),
				interval.upper().map(b -> shiftBound(b, delta)));
	}

	private static <E extends OrderedAdditiveGroup<E>> Bound<E> shiftBound(final Bound<E> bound, final E delta)
	{
		E shifted = bound.value().add(delta);
		return switch (bound)
		{
			case Bound.Closed<E> ignored -> new Bound.Closed<>(shifted);
			case Bound.Open<E> ignored -> new Bound.Open<>(shifted);
		};
	}

	public static <E extends OrderedEuclideanDomain<E>> E midpoint(final BoundedInterval<E> interval,
	                                                               final RoundingStrategy<E> strategy)
	{
		return interval.lower().value().add(interval.upper().value()).divide(2L, strategy).quotient();
	}

	private AlgebraicIntervals()
	{
	}
}
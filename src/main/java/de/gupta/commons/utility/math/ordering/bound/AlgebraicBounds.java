package de.gupta.commons.utility.math.ordering.bound;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.BoundedInterval;
import de.gupta.commons.utility.math.ordering.element.interval.Interval;
import de.gupta.commons.utility.math.ordering.element.interval.UnboundedInterval;

import java.util.Optional;

public final class AlgebraicBounds
{
	public static <F extends TotallyOrdered<F>> boolean subsumesFromBelow(final Interval<F> first,
	                                                                      final Interval<F> second)
	{
		return switch (first)
		{
			case BoundedInterval<F> b -> switch (second)
			{
				case BoundedInterval<F> b2 -> subsumesFromBelow(b.lower(), b2.lower());
				case UnboundedInterval<F> u -> subsumesFromBelow(b.lower(), u.lower());
			};
			case UnboundedInterval<F> u -> switch (second)
			{
				case BoundedInterval<F> b -> u.lower().map(l -> subsumesFromBelow(l, b.lower())).orElse(true);
				case UnboundedInterval<F> u2 -> u.lower().map(l -> subsumesFromBelow(l, u2.lower())).orElse(true);
			};
		};
	}

	public static <F extends TotallyOrdered<F>> boolean subsumesFromBelow(final Bound<F> first, final Bound<F> second)
	{
		return switch (first)
		{
			case Bound.Closed<F> c -> c.value().compare(second.value()).isLessThanOrEqualTo();
			case Bound.Open<F> o -> second.isClosed() ? o.value().compare(second.value()).isLessThan() :
					o.value().compare(second.value()).isLessThanOrEqualTo();
		};
	}

	public static <F extends TotallyOrdered<F>> boolean subsumesFromBelow(final Bound<F> first,
	                                                                      final Optional<Bound<F>> second)
	{
		return second.map(s -> subsumesFromBelow(first, s)).orElse(false);
	}

	public static <F extends TotallyOrdered<F>> boolean subsumesFromAbove(final Bound<F> first,
	                                                                      final Optional<Bound<F>> second)
	{
		return second.map(s -> subsumesFromAbove(first, s)).orElse(false);
	}

	public static <F extends TotallyOrdered<F>> boolean subsumesFromAbove(final Bound<F> first, final Bound<F> second)
	{
		return switch (first)
		{
			case Bound.Closed<F> c -> c.value().compare(second.value()).isGreaterThanOrEqualTo();
			case Bound.Open<F> o -> second.isClosed() ? o.value().compare(second.value()).isGreaterThan() :
					o.value().compare(second.value()).isGreaterThanOrEqualTo();
		};
	}
}
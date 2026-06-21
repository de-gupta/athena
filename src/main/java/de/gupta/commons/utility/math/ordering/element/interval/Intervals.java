package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

import java.util.Optional;

public final class Intervals
{
	public static <E extends TotallyOrdered<E>> BoundedInterval<E> open(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	private static <E extends TotallyOrdered<E>> BoundedInterval<E> bounded(final Bound<E> lower,
	                                                                        final Bound<E> upper)
	{
		return switch (lower.value().compare(upper.value()))
		{
			case LESS_THAN -> BoundedIntervalImpl.of(lower, upper);
			case GREATER_THAN -> throw new IllegalArgumentException(
					"Lower bound must not exceed upper bound: " + lower.value() + " > " + upper.value());
			case EQUAL ->
			{
				if (lower.isClosed() && upper.isClosed()) yield BoundedIntervalImpl.of(lower, upper);
				throw new IllegalArgumentException("Open or half-open interval with equal bounds is empty.");
			}
		};
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> closedOpen(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Open<>(upper));
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> openClosed(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Closed<>(upper));
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> point(final E value)
	{
		return closed(value, value);
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> closed(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Closed<>(upper));
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> atLeast(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Closed<>(lower)), Optional.empty());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> greaterThan(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Open<>(lower)), Optional.empty());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> atMost(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Closed<>(upper)));
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> lessThan(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Open<>(upper)));
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> all()
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.empty());
	}

	static <E extends TotallyOrdered<E>> Optional<BoundedInterval<E>> ofOptional(final Bound<E> lower,
	                                                                             final Bound<E> upper)
	{
		return switch (lower.value().compare(upper.value()))
		{
			case LESS_THAN -> Optional.of(BoundedIntervalImpl.of(lower, upper));
			case GREATER_THAN -> Optional.empty();
			case EQUAL -> lower.isClosed() && upper.isClosed()
					? Optional.of(BoundedIntervalImpl.of(lower, upper))
					: Optional.empty();
		};
	}

	private Intervals()
	{
	}
}
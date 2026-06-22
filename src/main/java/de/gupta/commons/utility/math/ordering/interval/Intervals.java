package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public final class Intervals
{
	// --- Element-side factories: E carries its own ordering ---

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> open(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Open<>(upper), IntervalOrderStructure.forElements());
	}

	static <E> BoundedInterval<E> bounded(final Bound<E> lower, final Bound<E> upper,
	                                      final IntervalOrderStructure<E> ios)
	{
		return switch (ios.compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> BoundedIntervalImpl.of(lower, upper, ios);
			case GREATER_THAN -> throw new IllegalArgumentException("Lower bound must not exceed upper bound.");
			case EQUAL ->
			{
				if (lower.isClosed() && upper.isClosed()) yield BoundedIntervalImpl.of(lower, upper, ios);
				throw new IllegalArgumentException("Open or half-open interval with equal bounds is empty.");
			}
		};
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> closedOpen(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Open<>(upper), IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> openClosed(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Closed<>(upper), IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> point(final E value)
	{
		return closed(value, value);
	}

	public static <E extends TotallyOrdered<E>> BoundedInterval<E> closed(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Closed<>(upper), IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> atLeast(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Closed<>(lower)), Optional.empty(),
				IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> greaterThan(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Open<>(lower)), Optional.empty(),
				IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> atMost(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Closed<>(upper)),
				IntervalOrderStructure.forElements());
	}

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> lessThan(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Open<>(upper)),
				IntervalOrderStructure.forElements());
	}

	// --- Structure-side: ordering provided explicitly ---

	public static <E extends TotallyOrdered<E>> UnboundedInterval<E> all()
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.empty(), TotallyOrdered::compare);
	}

	public static <E> ForOrder<E> over(final TotalOrderStructure<E> order)
	{
		return new ForOrder<>(IntervalOrderStructure.of(order));
	}

	// --- Shared validation ---

	private Intervals()
	{
	}

	public record ForOrder<E>(IntervalOrderStructure<E> ios)
	{
		public BoundedInterval<E> open(final E lower, final E upper)
		{
			return bounded(new Bound.Open<>(lower), new Bound.Open<>(upper), ios);
		}

		public BoundedInterval<E> closedOpen(final E lower, final E upper)
		{
			return bounded(new Bound.Closed<>(lower), new Bound.Open<>(upper), ios);
		}

		public BoundedInterval<E> openClosed(final E lower, final E upper)
		{
			return bounded(new Bound.Open<>(lower), new Bound.Closed<>(upper), ios);
		}

		public BoundedInterval<E> point(final E value)
		{
			return closed(value, value);
		}

		public BoundedInterval<E> closed(final E lower, final E upper)
		{
			return bounded(new Bound.Closed<>(lower), new Bound.Closed<>(upper), ios);
		}

		public UnboundedInterval<E> atLeast(final E lower)
		{
			return UnboundedIntervalImpl.of(Optional.of(new Bound.Closed<>(lower)), Optional.empty(), ios);
		}

		public UnboundedInterval<E> greaterThan(final E lower)
		{
			return UnboundedIntervalImpl.of(Optional.of(new Bound.Open<>(lower)), Optional.empty(), ios);
		}

		public UnboundedInterval<E> atMost(final E upper)
		{
			return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Closed<>(upper)), ios);
		}

		public UnboundedInterval<E> lessThan(final E upper)
		{
			return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Open<>(upper)), ios);
		}

		public UnboundedInterval<E> all()
		{
			return UnboundedIntervalImpl.of(Optional.empty(), Optional.empty(), ios);
		}
	}
}
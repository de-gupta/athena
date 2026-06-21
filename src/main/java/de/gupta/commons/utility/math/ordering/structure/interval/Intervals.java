package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public record Intervals<E>(TotalOrderStructure<E> order)
{
	public BoundedInterval<E> open(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	private BoundedInterval<E> bounded(final Bound<E> lower, final Bound<E> upper)
	{
		var ios = ios();
		return switch (ios.compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> BoundedIntervalImpl.of(lower, upper);
			case GREATER_THAN -> throw new IllegalArgumentException("Lower bound must not exceed upper bound.");
			case EQUAL ->
			{
				if (lower.isClosed() && upper.isClosed()) yield BoundedIntervalImpl.of(lower, upper);
				throw new IllegalArgumentException("Open or half-open interval with equal bounds is empty.");
			}
		};
	}

	private IntervalOrderStructure<E> ios()
	{
		return IntervalOrderStructure.of(order);
	}

	public BoundedInterval<E> closedOpen(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Open<>(upper));
	}

	public BoundedInterval<E> openClosed(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Closed<>(upper));
	}

	public BoundedInterval<E> point(final E value)
	{
		return closed(value, value);
	}

	public BoundedInterval<E> closed(final E lower, final E upper)
	{
		return bounded(new Bound.Closed<>(lower), new Bound.Closed<>(upper));
	}

	public UnboundedInterval<E> atLeast(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Closed<>(lower)), Optional.empty());
	}

	public UnboundedInterval<E> greaterThan(final E lower)
	{
		return UnboundedIntervalImpl.of(Optional.of(new Bound.Open<>(lower)), Optional.empty());
	}

	public UnboundedInterval<E> atMost(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Closed<>(upper)));
	}

	public UnboundedInterval<E> lessThan(final E upper)
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.of(new Bound.Open<>(upper)));
	}

	public UnboundedInterval<E> all()
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.empty());
	}
}
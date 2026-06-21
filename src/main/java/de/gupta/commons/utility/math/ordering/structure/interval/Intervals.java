package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public record Intervals<E>(TotalOrderStructure<E> order)
{
	public BoundedInterval<E> open(final E lower, final E upper)
	{
		return helper().bounded(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	private IntervalHelper<E> helper()
	{
		return new IntervalHelper<>(order);
	}

	public BoundedInterval<E> closedOpen(final E lower, final E upper)
	{
		return helper().bounded(new Bound.Closed<>(lower), new Bound.Open<>(upper));
	}

	public BoundedInterval<E> openClosed(final E lower, final E upper)
	{
		return helper().bounded(new Bound.Open<>(lower), new Bound.Closed<>(upper));
	}

	public BoundedInterval<E> point(final E value)
	{
		return closed(value, value);
	}

	public BoundedInterval<E> closed(final E lower, final E upper)
	{
		return helper().bounded(new Bound.Closed<>(lower), new Bound.Closed<>(upper));
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
package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public record BoundedInterval<E extends TotallyOrdered<E>>(Bound<E> lower, Bound<E> upper) implements Interval<E>
{
	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isPoint()
	{
		return lower.isClosed() && upper.isClosed() && lower.value().compare(upper.value()).isEqualTo();
	}

	@Override
	public boolean contains(final E element)
	{
		return lowerSatisfied(element) && upperSatisfied(element);
	}

	@Override
	public boolean contains(final Interval<E> other)
	{
		return switch (other)
		{
			case EmptyInterval<E> ignored -> true;
			case BoundedInterval<E> b -> lowerAtMost(lower, b.lower) && upperAtLeast(upper, b.upper);
		};
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return switch (other)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> b -> !endsBefore(upper, b.lower) && !endsBefore(b.upper, lower);
		};
	}

	@Override
	public Interval<E> intersect(final Interval<E> other)
	{
		return switch (other)
		{
			case EmptyInterval<E> e -> e;
			case BoundedInterval<E> b ->
			{
				Bound<E> newLower = maxLower(lower, b.lower);
				Bound<E> newUpper = minUpper(upper, b.upper);
				yield Intervals.of(newLower, newUpper);
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return switch (other)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> b -> touchesAt(upper, b.lower) || touchesAt(b.upper, lower);
		};
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return Optional.of(lower);
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return Optional.of(upper);
	}

	private static <E extends TotallyOrdered<E>> boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return end.value().compare(start.value()).isEqualTo() && end.isClosed() != start.isClosed();
	}

	static <E extends TotallyOrdered<E>> Bound<E> maxLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	static <E extends TotallyOrdered<E>> Bound<E> minUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	private static <E extends TotallyOrdered<E>> boolean endsBefore(final Bound<E> end, final Bound<E> start)
	{
		return switch (end.value().compare(start.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> end.isOpen() || start.isOpen();
		};
	}

	private static <E extends TotallyOrdered<E>> boolean lowerAtMost(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	private static <E extends TotallyOrdered<E>> boolean upperAtLeast(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	private boolean lowerSatisfied(final E element)
	{
		return switch (lower)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isGreaterThan();
		};
	}

	private boolean upperSatisfied(final E element)
	{
		return switch (upper)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isLessThan();
		};
	}

}
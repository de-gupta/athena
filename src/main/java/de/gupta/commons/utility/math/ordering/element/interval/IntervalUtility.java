package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

final class IntervalUtility
{
	static <E extends TotallyOrdered<E>> Bound<E> minLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isClosed() || b.isClosed()) ? new Bound.Closed<>(a.value()) : a;
		};
	}

	static <E extends TotallyOrdered<E>> Bound<E> maxUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isClosed() || b.isClosed()) ? new Bound.Closed<>(a.value()) : a;
		};
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

	static <E extends TotallyOrdered<E>> boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (lower.value().compare(upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	// TODO: bad method name and param names. which one is being checked and which one is the bound?
	static <E extends TotallyOrdered<E>> boolean lowerAtMost(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	// TODO: bad method name and param names. which one is being checked and which one is the bound?
	static <E extends TotallyOrdered<E>> boolean upperAtLeast(final Bound<E> a, final Bound<E> b)
	{
		return switch (a.value().compare(b.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	static <E extends TotallyOrdered<E>> boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return end.value().compare(start.value()).isEqualTo() && end.isClosed() != start.isClosed();
	}

	static <E extends TotallyOrdered<E>> boolean endsBefore(final Bound<E> end, final Bound<E> start)
	{
		return switch (end.value().compare(start.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> end.isOpen() || start.isOpen();
		};
	}

	private IntervalUtility()
	{
	}
}
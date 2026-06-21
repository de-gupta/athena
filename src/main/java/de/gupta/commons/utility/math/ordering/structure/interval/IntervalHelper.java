package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

record IntervalHelper<E>(TotalOrderStructure<E> order)
{
	boolean isAboveLower(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> order.compare(element, b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> order.compare(element, b.value()).isGreaterThan();
		};
	}

	boolean isBelowUpper(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> order.compare(element, b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> order.compare(element, b.value()).isLessThan();
		};
	}

	boolean lowerCoversLower(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (order.compare(outer.value(), inner.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	boolean upperCoversUpper(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (order.compare(outer.value(), inner.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	boolean endsBefore(final Bound<E> end, final Bound<E> start)
	{
		return switch (order.compare(end.value(), start.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> end.isOpen() || start.isOpen();
		};
	}

	boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return order.compare(end.value(), start.value()).isEqualTo() && end.isClosed() != start.isClosed();
	}

	boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (order.compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	Bound<E> maxLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	Bound<E> minUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	BoundedInterval<E> bounded(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (order.compare(lower.value(), upper.value()))
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
}

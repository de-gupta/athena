package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

// TODO: clean up duplication and method names
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

	// outer lower covers inner lower: outer admits all elements that inner admits at the lower end
	static <E extends TotallyOrdered<E>> boolean lowerCoversLower(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (outer.value().compare(inner.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	// outer upper covers inner upper: outer admits all elements that inner admits at the upper end
	static <E extends TotallyOrdered<E>> boolean upperCoversUpper(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (outer.value().compare(inner.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
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
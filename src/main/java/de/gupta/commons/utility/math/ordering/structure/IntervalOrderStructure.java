package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;

@FunctionalInterface
public interface IntervalOrderStructure<E> extends TotalOrderStructure<E>
{
	static <E> IntervalOrderStructure<E> of(final TotalOrderStructure<E> order)
	{
		return order::compare;
	}

	static <E extends TotallyOrdered<E>> IntervalOrderStructure<E> forElements()
	{
		return TotallyOrdered::compare;
	}

	default boolean boundHarboursElementFromBelow(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> compare(element, b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> compare(element, b.value()).isGreaterThan();
		};
	}

	default boolean boundHarboursElementFromAbove(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> compare(element, b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> compare(element, b.value()).isLessThan();
		};
	}

	// TODO: rename
	default boolean lowerCoversLower(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (compare(outer.value(), inner.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	// TODO: rename
	default boolean upperCoversUpper(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (compare(outer.value(), inner.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	// TODO: rename
	default boolean endsBefore(final Bound<E> end, final Bound<E> start)
	{
		return switch (compare(end.value(), start.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> end.isOpen() || start.isOpen();
		};
	}

	default boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return compare(end.value(), start.value()).isEqualTo() && end.isClosed() != start.isClosed();
	}

	default boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	// TODO: duplicate logic as minUpper, also rename and move to utility somewhere
	default Bound<E> maxLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (compare(a.value(), b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	default Bound<E> minUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (compare(a.value(), b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	// TODO: duplicate logic as maxUpper, also rename and move to utility somewhere
	default Bound<E> minLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (compare(a.value(), b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isClosed() || b.isClosed()) ? new Bound.Closed<>(a.value()) : a;
		};
	}

	default Bound<E> maxUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (compare(a.value(), b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isClosed() || b.isClosed()) ? new Bound.Closed<>(a.value()) : a;
		};
	}
}
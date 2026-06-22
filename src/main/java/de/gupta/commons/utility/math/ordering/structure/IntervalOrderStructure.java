package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.bound.Bound;
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

	// Does the container's lower bound admit everything the enclosed's lower bound admits?
	default boolean enclosesLowerBound(final Bound<E> container, final Bound<E> enclosed)
	{
		return switch (compare(container.value(), enclosed.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> container.isClosed() || enclosed.isOpen();
		};
	}

	// Does the container's upper bound admit everything the enclosed's upper bound admits?
	default boolean enclosesUpperBound(final Bound<E> container, final Bound<E> enclosed)
	{
		return switch (compare(container.value(), enclosed.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> container.isClosed() || enclosed.isOpen();
		};
	}

	// Is the end of one interval strictly before the start of another (no overlap, no touching)?
	default boolean endPrecedesStart(final Bound<E> end, final Bound<E> start)
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

	// Intersection selects the tightest bounds — used in intersect()
	default Bound<E> tightestLowerBound(final Bound<E> a, final Bound<E> b)
	{
		return tighterBound(a, b, true);
	}

	private Bound<E> tighterBound(final Bound<E> a, final Bound<E> b, final boolean greaterIsTighter)
	{
		return switch (compare(a.value(), b.value()))
		{
			case GREATER_THAN -> greaterIsTighter ? a : b;
			case LESS_THAN -> greaterIsTighter ? b : a;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	default Bound<E> tightestUpperBound(final Bound<E> a, final Bound<E> b)
	{
		return tighterBound(a, b, false);
	}

	// Span/hull selects the loosest bounds — used in span()
	default Bound<E> loosestLowerBound(final Bound<E> a, final Bound<E> b)
	{
		return looserBound(a, b, false);
	}

	private Bound<E> looserBound(final Bound<E> a, final Bound<E> b, final boolean greaterIsLooser)
	{
		return switch (compare(a.value(), b.value()))
		{
			case GREATER_THAN -> greaterIsLooser ? a : b;
			case LESS_THAN -> greaterIsLooser ? b : a;
			case EQUAL -> (a.isClosed() || b.isClosed()) ? new Bound.Closed<>(a.value()) : a;
		};
	}

	default Bound<E> loosestUpperBound(final Bound<E> a, final Bound<E> b)
	{
		return looserBound(a, b, true);
	}
}
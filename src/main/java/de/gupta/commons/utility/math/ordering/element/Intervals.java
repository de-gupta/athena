package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Bound;

public final class Intervals
{
	public static <E extends TotallyOrdered<E>> Interval<E> empty()
	{
		return new EmptyInterval<>();
	}

	public static <E extends TotallyOrdered<E>> Interval<E> open(final E lower, final E upper)
	{
		return of(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	static <E extends TotallyOrdered<E>> Interval<E> of(final Bound<E> lower, final Bound<E> upper)
	{
		return isNonEmpty(lower, upper) ? new BoundedInterval<>(lower, upper) : new EmptyInterval<>();
	}

	private static <E extends TotallyOrdered<E>> boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (lower.value().compare(upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	public static <E extends TotallyOrdered<E>> Interval<E> closedOpen(final E lower, final E upper)
	{
		return of(new Bound.Closed<>(lower), new Bound.Open<>(upper));
	}

	public static <E extends TotallyOrdered<E>> Interval<E> openClosed(final E lower, final E upper)
	{
		return of(new Bound.Open<>(lower), new Bound.Closed<>(upper));
	}

	public static <E extends TotallyOrdered<E>> Interval<E> point(final E value)
	{
		return closed(value, value);
	}

	public static <E extends TotallyOrdered<E>> Interval<E> closed(final E lower, final E upper)
	{
		return of(new Bound.Closed<>(lower), new Bound.Closed<>(upper));
	}

	private Intervals()
	{
	}
}
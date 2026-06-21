package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.Bound;

public record IntervalStructure<E>(TotalOrderStructure<E> order)
{
	// --- Factories ---

	public Interval<E> open(final E lower, final E upper)
	{
		return of(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	private Interval<E> of(final Bound<E> lower, final Bound<E> upper)
	{
		return isNonEmpty(lower, upper) ? new BoundedInterval<>(lower, upper) : new EmptyInterval<>();
	}

	private boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (order.compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	public Interval<E> closedOpen(final E lower, final E upper)
	{
		return of(new Bound.Closed<>(lower), new Bound.Open<>(upper));
	}

	public Interval<E> openClosed(final E lower, final E upper)
	{
		return of(new Bound.Open<>(lower), new Bound.Closed<>(upper));
	}

	public Interval<E> point(final E value)
	{
		return closed(value, value);
	}

	// --- Operations ---

	public Interval<E> closed(final E lower, final E upper)
	{
		return of(new Bound.Closed<>(lower), new Bound.Closed<>(upper));
	}

	public Interval<E> empty()
	{
		return new EmptyInterval<>();
	}

	public boolean contains(final Interval<E> interval, final E element)
	{
		return switch (interval)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> b -> lowerSatisfied(b.lower(), element) && upperSatisfied(b.upper(), element);
		};
	}

	private boolean lowerSatisfied(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> order.compare(element, b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> order.compare(element, b.value()).isGreaterThan();
		};
	}

	private boolean upperSatisfied(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> order.compare(element, b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> order.compare(element, b.value()).isLessThan();
		};
	}

	public boolean contains(final Interval<E> outer, final Interval<E> inner)
	{
		return switch (outer)
		{
			case EmptyInterval<E> ignored -> inner.isEmpty();
			case BoundedInterval<E> a -> switch (inner)
			{
				case EmptyInterval<E> ignored -> true;
				case BoundedInterval<E> b -> lowerAtMost(a.lower(), b.lower()) && upperAtLeast(a.upper(), b.upper());
			};
		};
	}

	// --- Helpers ---

	private boolean lowerAtMost(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	private boolean upperAtLeast(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> a.isClosed() || b.isOpen();
		};
	}

	public boolean overlaps(final Interval<E> a, final Interval<E> b)
	{
		return switch (a)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> ba -> switch (b)
			{
				case EmptyInterval<E> ignored -> false;
				case BoundedInterval<E> bb ->
						!endsBefore(ba.upper(), bb.lower()) && !endsBefore(bb.upper(), ba.lower());
			};
		};
	}

	private boolean endsBefore(final Bound<E> myUpper, final Bound<E> theirLower)
	{
		return switch (order.compare(myUpper.value(), theirLower.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> myUpper.isOpen() || theirLower.isOpen();
		};
	}

	public Interval<E> intersect(final Interval<E> a, final Interval<E> b)
	{
		return switch (a)
		{
			case EmptyInterval<E> e -> e;
			case BoundedInterval<E> ba -> switch (b)
			{
				case EmptyInterval<E> e -> e;
				case BoundedInterval<E> bb ->
				{
					Bound<E> newLower = maxLower(ba.lower(), bb.lower());
					Bound<E> newUpper = minUpper(ba.upper(), bb.upper());
					yield of(newLower, newUpper);
				}
			};
		};
	}

	private Bound<E> maxLower(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case GREATER_THAN -> a;
			case LESS_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	private Bound<E> minUpper(final Bound<E> a, final Bound<E> b)
	{
		return switch (order.compare(a.value(), b.value()))
		{
			case LESS_THAN -> a;
			case GREATER_THAN -> b;
			case EQUAL -> (a.isOpen() || b.isOpen()) ? new Bound.Open<>(a.value()) : a;
		};
	}

	public boolean abuts(final Interval<E> a, final Interval<E> b)
	{
		return switch (a)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> ba -> switch (b)
			{
				case EmptyInterval<E> ignored -> false;
				case BoundedInterval<E> bb -> touchesAt(ba.upper(), bb.lower()) || touchesAt(bb.upper(), ba.lower());
			};
		};
	}

	private boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return order.compare(end.value(), start.value()).isEqualTo()
				&& end.isClosed() != start.isClosed();
	}

	public boolean isPoint(final Interval<E> interval)
	{
		return switch (interval)
		{
			case EmptyInterval<E> ignored -> false;
			case BoundedInterval<E> b -> b.lower().isClosed() && b.upper().isClosed()
					&& order.compare(b.lower().value(), b.upper().value()).isEqualTo();
		};
	}

}
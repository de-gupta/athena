package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public record IntervalStructure<E>(TotalOrderStructure<E> order)
{
	// --- Bounded factories ---

	public BoundedInterval<E> open(final E lower, final E upper)
	{
		return bounded(new Bound.Open<>(lower), new Bound.Open<>(upper));
	}

	private BoundedInterval<E> bounded(final Bound<E> lower, final Bound<E> upper)
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

	// --- Unbounded factories ---

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

	// --- Operations ---

	public UnboundedInterval<E> all()
	{
		return UnboundedIntervalImpl.of(Optional.empty(), Optional.empty());
	}

	public boolean contains(final Interval<E> interval, final E element)
	{
		return switch (interval)
		{
			case BoundedInterval<E> b -> isAboveLower(b.lower(), element) && isBelowUpper(b.upper(), element);
			case UnboundedInterval<E> u -> u.lower().map(b -> isAboveLower(b, element)).orElse(true)
					&& u.upper().map(b -> isBelowUpper(b, element)).orElse(true);
		};
	}

	private boolean isAboveLower(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> order.compare(element, b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> order.compare(element, b.value()).isGreaterThan();
		};
	}

	private boolean isBelowUpper(final Bound<E> bound, final E element)
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
			case BoundedInterval<E> a -> switch (inner)
			{
				case BoundedInterval<E> b ->
						lowerCoversLower(a.lower(), b.lower()) && upperCoversUpper(a.upper(), b.upper());
				case UnboundedInterval<E> ignored -> false;
			};
			case UnboundedInterval<E> u -> switch (inner)
			{
				case BoundedInterval<E> b -> u.lower().map(l -> lowerCoversLower(l, b.lower())).orElse(true)
						&& u.upper().map(ul -> upperCoversUpper(ul, b.upper())).orElse(true);
				case UnboundedInterval<E> ui ->
						(u.lower().isEmpty() || ui.lower().isPresent() && lowerCoversLower(u.lower().get(),
								ui.lower().get()))
								&& (u.upper().isEmpty() || ui.upper().isPresent() && upperCoversUpper(u.upper().get(),
								ui.upper().get()));
			};
		};
	}

	private boolean lowerCoversLower(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (order.compare(outer.value(), inner.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	// --- Private helpers ---

	private boolean upperCoversUpper(final Bound<E> outer, final Bound<E> inner)
	{
		return switch (order.compare(outer.value(), inner.value()))
		{
			case GREATER_THAN -> true;
			case LESS_THAN -> false;
			case EQUAL -> outer.isClosed() || inner.isOpen();
		};
	}

	public boolean overlaps(final Interval<E> a, final Interval<E> b)
	{
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb ->
						!endsBefore(ba.upper(), bb.lower()) && !endsBefore(bb.upper(), ba.lower());
				case UnboundedInterval<E> u ->
				{
					boolean boundedBeforeUnbounded = u.lower().isPresent() && endsBefore(ba.upper(), u.lower().get());
					boolean unboundedBeforeBounded = u.upper().isPresent() && endsBefore(u.upper().get(), ba.lower());
					yield !boundedBeforeUnbounded && !unboundedBeforeBounded;
				}
			};
			case UnboundedInterval<E> ignored -> overlaps(b, a);
		};
	}

	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> a, final BoundedInterval<E> b)
	{
		Bound<E> newLower = maxLower(a.lower(), b.lower());
		Bound<E> newUpper = minUpper(a.upper(), b.upper());
		return isNonEmpty(newLower, newUpper) ? Optional.of(BoundedIntervalImpl.of(newLower, newUpper)) :
				Optional.empty();
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

	private boolean isNonEmpty(final Bound<E> lower, final Bound<E> upper)
	{
		return switch (order.compare(lower.value(), upper.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> lower.isClosed() && upper.isClosed();
		};
	}

	public boolean abuts(final Interval<E> a, final Interval<E> b)
	{
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb -> touchesAt(ba.upper(), bb.lower()) || touchesAt(bb.upper(), ba.lower());
				case UnboundedInterval<E> u -> u.lower().map(l -> touchesAt(ba.upper(), l)).orElse(false)
						|| u.upper().map(u2 -> touchesAt(u2, ba.lower())).orElse(false);
			};
			case UnboundedInterval<E> ignored -> abuts(b, a);
		};
	}

	public boolean isPoint(final BoundedInterval<E> interval)
	{
		return interval.lower().isClosed() && interval.upper().isClosed()
				&& order.compare(interval.lower().value(), interval.upper().value()).isEqualTo();
	}

	private boolean endsBefore(final Bound<E> end, final Bound<E> start)
	{
		return switch (order.compare(end.value(), start.value()))
		{
			case LESS_THAN -> true;
			case GREATER_THAN -> false;
			case EQUAL -> end.isOpen() || start.isOpen();
		};
	}

	private boolean touchesAt(final Bound<E> end, final Bound<E> start)
	{
		return order.compare(end.value(), start.value()).isEqualTo() && end.isClosed() != start.isClosed();
	}
}
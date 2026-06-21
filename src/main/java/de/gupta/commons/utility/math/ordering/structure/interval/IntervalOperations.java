package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public record IntervalOperations<E>(TotalOrderStructure<E> order)
{
	public boolean contains(final Interval<E> interval, final E element)
	{
		var h = helper();
		return switch (interval)
		{
			case BoundedInterval<E> b -> h.isAboveLower(b.lower(), element) && h.isBelowUpper(b.upper(), element);
			case UnboundedInterval<E> u -> u.lower().map(b -> h.isAboveLower(b, element)).orElse(true)
					&& u.upper().map(b -> h.isBelowUpper(b, element)).orElse(true);
		};
	}

	private IntervalHelper<E> helper()
	{
		return new IntervalHelper<>(order);
	}

	public boolean contains(final Interval<E> outer, final Interval<E> inner)
	{
		var h = helper();
		return switch (outer)
		{
			case BoundedInterval<E> a -> switch (inner)
			{
				case BoundedInterval<E> b ->
						h.lowerCoversLower(a.lower(), b.lower()) && h.upperCoversUpper(a.upper(), b.upper());
				case UnboundedInterval<E> ignored -> false;
			};
			case UnboundedInterval<E> u -> switch (inner)
			{
				case BoundedInterval<E> b -> u.lower().map(l -> h.lowerCoversLower(l, b.lower())).orElse(true)
						&& u.upper().map(ul -> h.upperCoversUpper(ul, b.upper())).orElse(true);
				case UnboundedInterval<E> ui ->
						(u.lower().isEmpty() || ui.lower().isPresent() && h.lowerCoversLower(u.lower().get(),
								ui.lower().get()))
								&& (u.upper().isEmpty() || ui.upper().isPresent() && h.upperCoversUpper(u.upper().get(),
								ui.upper().get()));
			};
		};
	}

	public boolean overlaps(final Interval<E> a, final Interval<E> b)
	{
		var h = helper();
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb ->
						!h.endsBefore(ba.upper(), bb.lower()) && !h.endsBefore(bb.upper(), ba.lower());
				case UnboundedInterval<E> u ->
				{
					boolean boundedBeforeUnbounded = u.lower().isPresent() && h.endsBefore(ba.upper(), u.lower().get());
					boolean unboundedBeforeBounded = u.upper().isPresent() && h.endsBefore(u.upper().get(), ba.lower());
					yield !boundedBeforeUnbounded && !unboundedBeforeBounded;
				}
			};
			case UnboundedInterval<E> ignored -> overlaps(b, a);
		};
	}

	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> a, final BoundedInterval<E> b)
	{
		var h = helper();
		Bound<E> newLower = h.maxLower(a.lower(), b.lower());
		Bound<E> newUpper = h.minUpper(a.upper(), b.upper());
		return h.isNonEmpty(newLower, newUpper) ? Optional.of(BoundedIntervalImpl.of(newLower, newUpper)) :
				Optional.empty();
	}

	public boolean abuts(final Interval<E> a, final Interval<E> b)
	{
		var h = helper();
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb ->
						h.touchesAt(ba.upper(), bb.lower()) || h.touchesAt(bb.upper(), ba.lower());
				case UnboundedInterval<E> u -> u.lower().map(l -> h.touchesAt(ba.upper(), l)).orElse(false)
						|| u.upper().map(u2 -> h.touchesAt(u2, ba.lower())).orElse(false);
			};
			case UnboundedInterval<E> ignored -> abuts(b, a);
		};
	}

	public boolean isPoint(final BoundedInterval<E> interval)
	{
		return interval.lower().isClosed() && interval.upper().isClosed()
				&& order.compare(interval.lower().value(), interval.upper().value()).isEqualTo();
	}
}
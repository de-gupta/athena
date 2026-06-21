package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.Bound;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;

import java.util.Optional;

public record IntervalOperations<E>(TotalOrderStructure<E> order)
{
	public boolean contains(final Interval<E> interval, final E element)
	{
		var ios = ios();
		return switch (interval)
		{
			case BoundedInterval<E> b ->
					ios.boundHarboursElementFromBelow(b.lower(), element) && ios.boundHarboursElementFromAbove(
							b.upper(), element);
			case UnboundedInterval<E> u ->
					u.lower().map(b -> ios.boundHarboursElementFromBelow(b, element)).orElse(true)
							&& u.upper().map(b -> ios.boundHarboursElementFromAbove(b, element)).orElse(true);
		};
	}

	private IntervalOrderStructure<E> ios()
	{
		return IntervalOrderStructure.of(order);
	}

	public boolean contains(final Interval<E> outer, final Interval<E> inner)
	{
		var ios = ios();
		return switch (outer)
		{
			case BoundedInterval<E> a -> switch (inner)
			{
				case BoundedInterval<E> b ->
						ios.lowerCoversLower(a.lower(), b.lower()) && ios.upperCoversUpper(a.upper(), b.upper());
				case UnboundedInterval<E> ignored -> false;
			};
			case UnboundedInterval<E> u -> switch (inner)
			{
				case BoundedInterval<E> b -> u.lower().map(l -> ios.lowerCoversLower(l, b.lower())).orElse(true)
						&& u.upper().map(ul -> ios.upperCoversUpper(ul, b.upper())).orElse(true);
				case UnboundedInterval<E> ui ->
						(u.lower().isEmpty() || ui.lower().isPresent() && ios.lowerCoversLower(u.lower().get(),
								ui.lower().get()))
								&& (u.upper().isEmpty() || ui.upper().isPresent() && ios.upperCoversUpper(
								u.upper().get(),
								ui.upper().get()));
			};
		};
	}

	public boolean overlaps(final Interval<E> a, final Interval<E> b)
	{
		var ios = ios();
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb ->
						!ios.endsBefore(ba.upper(), bb.lower()) && !ios.endsBefore(bb.upper(), ba.lower());
				case UnboundedInterval<E> u ->
				{
					boolean boundedBeforeUnbounded =
							u.lower().isPresent() && ios.endsBefore(ba.upper(), u.lower().get());
					boolean unboundedBeforeBounded =
							u.upper().isPresent() && ios.endsBefore(u.upper().get(), ba.lower());
					yield !boundedBeforeUnbounded && !unboundedBeforeBounded;
				}
			};
			case UnboundedInterval<E> ignored -> overlaps(b, a);
		};
	}

	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> a, final BoundedInterval<E> b)
	{
		var ios = ios();
		Bound<E> newLower = ios.maxLower(a.lower(), b.lower());
		Bound<E> newUpper = ios.minUpper(a.upper(), b.upper());
		return ios.isNonEmpty(newLower, newUpper) ? Optional.of(BoundedIntervalImpl.of(newLower, newUpper)) :
				Optional.empty();
	}

	public boolean abuts(final Interval<E> a, final Interval<E> b)
	{
		var ios = ios();
		return switch (a)
		{
			case BoundedInterval<E> ba -> switch (b)
			{
				case BoundedInterval<E> bb ->
						ios.touchesAt(ba.upper(), bb.lower()) || ios.touchesAt(bb.upper(), ba.lower());
				case UnboundedInterval<E> u -> u.lower().map(l -> ios.touchesAt(ba.upper(), l)).orElse(false)
						|| u.upper().map(u2 -> ios.touchesAt(u2, ba.lower())).orElse(false);
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
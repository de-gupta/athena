package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
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
							b.upper(),
							element);
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
						ios.enclosesLowerBound(a.lower(), b.lower()) && ios.enclosesUpperBound(a.upper(), b.upper());
				case UnboundedInterval<E> ignored -> false;
			};
			case UnboundedInterval<E> u -> switch (inner)
			{
				case BoundedInterval<E> b -> u.lower().map(l -> ios.enclosesLowerBound(l, b.lower())).orElse(true)
						&& u.upper().map(ul -> ios.enclosesUpperBound(ul, b.upper())).orElse(true);
				case UnboundedInterval<E> ui ->
						(u.lower().isEmpty() || ui.lower().isPresent() && ios.enclosesLowerBound(u.lower().get(),
								ui.lower().get()))
								&& (u.upper().isEmpty() || ui.upper().isPresent() && ios.enclosesUpperBound(
								u.upper().get(), ui.upper().get()));
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
						!ios.endPrecedesStart(ba.upper(), bb.lower()) && !ios.endPrecedesStart(bb.upper(), ba.lower());
				case UnboundedInterval<E> u ->
				{
					boolean boundedBeforeUnbounded = u.lower().isPresent() && ios.endPrecedesStart(ba.upper(),
							u.lower().get());
					boolean unboundedBeforeBounded = u.upper().isPresent() && ios.endPrecedesStart(u.upper().get(),
							ba.lower());
					yield !boundedBeforeUnbounded && !unboundedBeforeBounded;
				}
			};
			case UnboundedInterval<E> ua -> switch (b)
			{
				case BoundedInterval<E> ignored -> overlaps(b, a);
				case UnboundedInterval<E> ub ->
				{
					boolean myUpperBeforeTheirLower = ua.upper().isPresent() && ub.lower().isPresent()
							&& ios.endPrecedesStart(ua.upper().get(), ub.lower().get());
					boolean theirUpperBeforeMyLower = ub.upper().isPresent() && ua.lower().isPresent()
							&& ios.endPrecedesStart(ub.upper().get(), ua.lower().get());
					yield !myUpperBeforeTheirLower && !theirUpperBeforeMyLower;
				}
			};
		};
	}

	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> a, final BoundedInterval<E> b)
	{
		var ios = ios();
		Bound<E> newLower = ios.tightestLowerBound(a.lower(), b.lower());
		Bound<E> newUpper = ios.tightestUpperBound(a.upper(), b.upper());
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
			case UnboundedInterval<E> ua -> switch (b)
			{
				case BoundedInterval<E> ignored -> abuts(b, a);
				case UnboundedInterval<E> ub ->
				{
					boolean myUpperTouchesTheirLower = ua.upper().isPresent() && ub.lower().isPresent()
							&& ios.touchesAt(ua.upper().get(), ub.lower().get());
					boolean theirUpperTouchesMyLower = ub.upper().isPresent() && ua.lower().isPresent()
							&& ios.touchesAt(ub.upper().get(), ua.lower().get());
					yield myUpperTouchesTheirLower || theirUpperTouchesMyLower;
				}
			};
		};
	}

	public boolean isPoint(final BoundedInterval<E> interval)
	{
		return interval.lower().isClosed() && interval.upper().isClosed()
				&& order.compare(interval.lower().value(), interval.upper().value()).isEqualTo();
	}
}
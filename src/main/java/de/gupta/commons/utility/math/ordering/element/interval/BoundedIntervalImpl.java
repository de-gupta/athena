package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bounds;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;

import java.util.Optional;

record BoundedIntervalImpl<E extends TotallyOrdered<E>>(Bound<E> lower, Bound<E> upper) implements BoundedInterval<E>
{
	@Override
	public boolean isPoint()
	{
		return Bounds.areBothClosed(lower, upper) && Bounds.areValuesEqual(lower, upper);
	}

	@Override
	public boolean contains(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b ->
					ios().lowerCoversLower(lower, b.lower()) && ios().upperCoversUpper(upper, b.upper());
			case UnboundedInterval<E> _ -> false;
		};
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other)
	{
		var ios = ios();
		Bound<E> newLower = ios.maxLower(lower, other.lower());
		Bound<E> newUpper = ios.minUpper(upper, other.upper());
		return ios.isNonEmpty(newLower, newUpper) ? Optional.of(of(newLower, newUpper)) : Optional.empty();
	}

	static <E extends TotallyOrdered<E>> BoundedIntervalImpl<E> of(final Bound<E> lower, final Bound<E> upper)
	{
		return new BoundedIntervalImpl<>(lower, upper);
	}

	private IntervalOrderStructure<E> ios()
	{
		return IntervalOrderStructure.forElements();
	}

	@Override
	public boolean contains(final E element)
	{
		return ios().boundHarboursElementFromBelow(lower, element) && ios().boundHarboursElementFromAbove(upper,
				element);
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> !ios.endsBefore(upper, b.lower) && !ios.endsBefore(b.upper, lower);
			case UnboundedInterval<E> u ->
			{
				boolean beforeUnbounded = u.lower().isPresent() && ios.endsBefore(upper, u.lower().get());
				boolean afterUnbounded = u.upper().isPresent() && ios.endsBefore(u.upper().get(), lower);
				yield !beforeUnbounded && !afterUnbounded;
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> ios.touchesAt(upper, b.lower) || ios.touchesAt(b.upper, lower);
			case UnboundedInterval<E> u -> u.lower().map(l -> ios.touchesAt(upper, l)).orElse(false)
					|| u.upper().map(u2 -> ios.touchesAt(u2, lower)).orElse(false);
		};
	}

	@Override
	public Interval<E> span(final Interval<E> other)
	{
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> of(ios.minLower(lower, b.lower), ios.maxUpper(upper, b.upper));
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = u.lower().map(l -> ios.minLower(lower, l));
				Optional<Bound<E>> newUpper = u.upper().map(u2 -> ios.maxUpper(upper, u2));
				yield newLower.isPresent() && newUpper.isPresent()
						? of(newLower.get(), newUpper.get())
						: UnboundedIntervalImpl.of(newLower, newUpper);
			}
		};
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return Optional.of(lower);
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return Optional.of(upper);
	}
}
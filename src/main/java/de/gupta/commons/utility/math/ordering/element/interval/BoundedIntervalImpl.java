package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bounds;

import java.util.Optional;

record BoundedIntervalImpl<E extends TotallyOrdered<E>>(Bound<E> lower, Bound<E> upper) implements
		BoundedInterval<E>
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
			case BoundedInterval<E> bounded -> IntervalUtility.lowerCoversLower(lower,
					bounded.lower()) && IntervalUtility.upperCoversUpper(upper, bounded.upper());
			case UnboundedInterval<E> _ -> false;
		};
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other)
	{
		Bound<E> newLower = IntervalUtility.maxLower(lower, other.lower());
		Bound<E> newUpper = IntervalUtility.minUpper(upper, other.upper());

		return IntervalUtility.isNonEmpty(newLower, newUpper) ?
				Optional.of(of(newLower, newUpper)) : Optional.empty();
	}

	static <E extends TotallyOrdered<E>> BoundedIntervalImpl<E> of(final Bound<E> lower, final Bound<E> upper)
	{
		return new BoundedIntervalImpl<>(lower, upper);
	}

	@Override
	public boolean contains(final E element)
	{
		return isAboveLower(element) && isBelowUpper(element);
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b ->
					!IntervalUtility.endsBefore(upper, b.lower) && !IntervalUtility.endsBefore(b.upper, lower);
			case UnboundedInterval<E> u ->
			{
				boolean boundedBeforeUnbounded =
						u.lower().isPresent() && IntervalUtility.endsBefore(upper, u.lower().get());
				boolean unboundedBeforeBounded =
						u.upper().isPresent() && IntervalUtility.endsBefore(u.upper().get(), lower);
				yield !boundedBeforeUnbounded && !unboundedBeforeBounded;
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b ->
					IntervalUtility.touchesAt(upper, b.lower) || IntervalUtility.touchesAt(b.upper, lower);
			case UnboundedInterval<E> u -> u.lower().map(l -> IntervalUtility.touchesAt(upper, l)).orElse(false)
					|| u.upper().map(u2 -> IntervalUtility.touchesAt(u2, lower)).orElse(false);
		};
	}

	@Override
	public Interval<E> span(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> BoundedIntervalImpl.of(IntervalUtility.minLower(lower, b.lower),
					IntervalUtility.maxUpper(upper, b.upper));
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = u.lower().map(l -> IntervalUtility.minLower(lower, l));
				Optional<Bound<E>> newUpper = u.upper().map(u2 -> IntervalUtility.maxUpper(upper, u2));
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get())
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


	private boolean isAboveLower(final E element)
	{
		return switch (lower)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isGreaterThan();
		};
	}


	private boolean isBelowUpper(final E element)
	{
		return switch (upper)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isLessThan();
		};
	}
}
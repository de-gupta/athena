package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.bound.Bound;

import java.util.Optional;

record UnboundedIntervalImpl<E extends TotallyOrdered<E>>(Optional<Bound<E>> lower, Optional<Bound<E>> upper)
		implements UnboundedInterval<E>
{

	static <E extends TotallyOrdered<E>> UnboundedInterval<E> withLowerBound(final Bound<E> lowerBound)
	{
		return of(Optional.of(lowerBound), Optional.empty());
	}

	static <E extends TotallyOrdered<E>> UnboundedInterval<E> of(final Optional<Bound<E>> newLower,
	                                                             final Optional<Bound<E>> newUpper)
	{
		return new UnboundedIntervalImpl<>(newLower, newUpper);
	}

	static <E extends TotallyOrdered<E>> UnboundedInterval<E> withLowerBound(final Optional<Bound<E>> lowerBound)
	{
		return of(lowerBound, Optional.empty());
	}

	static <E extends TotallyOrdered<E>> UnboundedInterval<E> withUpperBound(final Bound<E> upperBound)
	{
		return of(Optional.empty(), Optional.of(upperBound));
	}

	static <E extends TotallyOrdered<E>> UnboundedInterval<E> withUpperBound(final Optional<Bound<E>> upperBound)
	{
		return of(Optional.empty(), upperBound);
	}

	@Override
	public boolean contains(final E element)
	{
		return lower.map(b -> lowerSatisfied(b, element)).orElse(true)
				&& upper.map(b -> upperSatisfied(b, element)).orElse(true);
	}

	private static <E extends TotallyOrdered<E>> boolean lowerSatisfied(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isGreaterThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isGreaterThan();
		};
	}

	private static <E extends TotallyOrdered<E>> boolean upperSatisfied(final Bound<E> bound, final E element)
	{
		return switch (bound)
		{
			case Bound.Closed<E> b -> element.compare(b.value()).isLessThanOrEqualTo();
			case Bound.Open<E> b -> element.compare(b.value()).isLessThan();
		};
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> b.overlaps(this);
			case UnboundedInterval<E> u ->
			{
				boolean myUpperBeforeTheirLower = upper.isPresent() && u.lower().isPresent()
						&& IntervalUtility.endsBefore(upper.get(), u.lower().get());
				boolean theirUpperBeforeMyLower = u.upper().isPresent() && lower.isPresent()
						&& IntervalUtility.endsBefore(u.upper().get(), lower.get());
				yield !myUpperBeforeTheirLower && !theirUpperBeforeMyLower;
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> b.abuts(this);
			case UnboundedInterval<E> u ->
			{
				boolean myUpperTouchesTheirLower = upper.isPresent() && u.lower().isPresent()
						&& IntervalUtility.touchesAt(upper.get(), u.lower().get());
				boolean theirUpperTouchesMyLower = u.upper().isPresent() && lower.isPresent()
						&& IntervalUtility.touchesAt(u.upper().get(), lower.get());
				yield myUpperTouchesTheirLower || theirUpperTouchesMyLower;
			}
		};
	}

	@Override
	public Interval<E> span(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedIntervalImpl<E> b ->
			{
				Optional<Bound<E>> newLower = lower.map(l -> IntervalUtility.minLower(l, b.lower()))
				                                   .or(() -> Optional.of(b.lower()));
				Optional<Bound<E>> newUpper = upper.map(u -> IntervalUtility.maxUpper(u, b.upper()))
				                                   .or(() -> Optional.of(b.upper()));
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get())
						: UnboundedIntervalImpl.of(newLower, newUpper);
			}
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = lower.isPresent() && u.lower().isPresent()
						? Optional.of(IntervalUtility.minLower(lower.get(), u.lower().get()))
						: Optional.empty();
				Optional<Bound<E>> newUpper = upper.isPresent() && u.upper().isPresent()
						? Optional.of(IntervalUtility.maxUpper(upper.get(), u.upper().get()))
						: Optional.empty();
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get())
						: UnboundedIntervalImpl.of(newLower, newUpper);
			}
		};
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return lower;
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return upper;
	}
}
package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;

import java.util.Objects;
import java.util.Optional;

record UnboundedIntervalImpl<E>(Optional<Bound<E>> lower, Optional<Bound<E>> upper, IntervalOrderStructure<E> ios)
		implements UnboundedInterval<E>
{
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof UnboundedIntervalImpl<?> other)) return false;
		return Objects.equals(lower, other.lower) && Objects.equals(upper, other.upper);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(lower, upper);
	}

	@Override
	public boolean contains(final E element)
	{
		return lower.map(b -> ios.boundHarboursElementFromBelow(b, element)).orElse(true)
				&& upper.map(b -> ios.boundHarboursElementFromAbove(b, element)).orElse(true);
	}

	@Override
	public boolean contains(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b -> lower.map(l -> ios.enclosesLowerBound(l, b.lower())).orElse(true)
					&& upper.map(u -> ios.enclosesUpperBound(u, b.upper())).orElse(true);
			case UnboundedInterval<E> u ->
					(lower.isEmpty() || u.lower().isPresent() && ios.enclosesLowerBound(lower.get(), u.lower().get()))
							&& (upper.isEmpty() || u.upper().isPresent() && ios.enclosesUpperBound(upper.get(),
							u.upper().get()));
		};
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b -> b.overlaps(this);
			case UnboundedInterval<E> u ->
			{
				boolean myUpperBeforeTheirLower = upper.isPresent() && u.lower().isPresent()
						&& ios.endPrecedesStart(upper.get(), u.lower().get());
				boolean theirUpperBeforeMyLower = u.upper().isPresent() && lower.isPresent()
						&& ios.endPrecedesStart(u.upper().get(), lower.get());
				yield !myUpperBeforeTheirLower && !theirUpperBeforeMyLower;
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b -> b.abuts(this);
			case UnboundedInterval<E> u ->
			{
				boolean myUpperTouchesTheirLower = upper.isPresent() && u.lower().isPresent()
						&& ios.touchesAt(upper.get(), u.lower().get());
				boolean theirUpperTouchesMyLower = u.upper().isPresent() && lower.isPresent()
						&& ios.touchesAt(u.upper().get(), lower.get());
				yield myUpperTouchesTheirLower || theirUpperTouchesMyLower;
			}
		};
	}

	@Override
	public Interval<E> span(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b ->
			{
				Optional<Bound<E>> newLower = lower.map(bound -> ios.loosestLowerBound(bound, b.lower()));
				Optional<Bound<E>> newUpper = upper.map(eBound -> ios.loosestUpperBound(eBound, b.upper()));
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get(), ios)
						: UnboundedIntervalImpl.of(newLower, newUpper, ios);
			}
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = lower.isPresent() && u.lower().isPresent()
						? Optional.of(ios.loosestLowerBound(lower.get(), u.lower().get())) : Optional.empty();
				Optional<Bound<E>> newUpper = upper.isPresent() && u.upper().isPresent()
						? Optional.of(ios.loosestUpperBound(upper.get(), u.upper().get())) : Optional.empty();
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get(), ios)
						: UnboundedIntervalImpl.of(newLower, newUpper, ios);
			}
		};
	}

	static <E> UnboundedIntervalImpl<E> of(final Optional<Bound<E>> lower, final Optional<Bound<E>> upper,
	                                       final IntervalOrderStructure<E> ios)
	{
		return new UnboundedIntervalImpl<>(lower, upper, ios);
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other)
	{
		Bound<E> newLower = lower.map(l -> ios.tightestLowerBound(l, other.lower())).orElse(other.lower());
		Bound<E> newUpper = upper.map(u -> ios.tightestUpperBound(u, other.upper())).orElse(other.upper());
		return ios.isNonEmpty(newLower, newUpper)
				? Optional.of(BoundedIntervalImpl.of(newLower, newUpper, ios))
				: Optional.empty();
	}

	@Override
	public Optional<Interval<E>> intersect(final Interval<E> other)
	{
		Optional<Bound<E>> newLower = lower.isPresent() && other.lowerBound().isPresent()
				? Optional.of(ios.tightestLowerBound(lower.get(), other.lowerBound().get()))
				: lower.isPresent() ? lower : other.lowerBound();
		Optional<Bound<E>> newUpper = upper.isPresent() && other.upperBound().isPresent()
				? Optional.of(ios.tightestUpperBound(upper.get(), other.upperBound().get()))
				: upper.isPresent() ? upper : other.upperBound();
		if (newLower.isPresent() && newUpper.isPresent())
			return ios.isNonEmpty(newLower.get(), newUpper.get())
					? Optional.of(BoundedIntervalImpl.of(newLower.get(), newUpper.get(), ios))
					: Optional.empty();
		return Optional.of(UnboundedIntervalImpl.of(newLower, newUpper, ios));
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

	@Override
	public Interval<E> closure()
	{
		return UnboundedIntervalImpl.of(lower.map(Bound::closure), upper.map(Bound::closure), ios);
	}

	UnboundedInterval<E> withBounds(final Optional<Bound<E>> newLower, final Optional<Bound<E>> newUpper)
	{
		return UnboundedIntervalImpl.of(newLower, newUpper, ios);
	}

	UnboundedIntervalImpl
	{
		if (lower.isPresent() && upper.isPresent())
			throw new IllegalArgumentException("Use BoundedInterval when both bounds are present.");
	}
}
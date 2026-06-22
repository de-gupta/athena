package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;

import java.util.Objects;
import java.util.Optional;

record BoundedIntervalImpl<E>(Bound<E> lower, Bound<E> upper, IntervalOrderStructure<E> ios)
		implements BoundedInterval<E>
{
	static <E> BoundedIntervalImpl<E> of(final Bound<E> lower, final Bound<E> upper,
	                                     final IntervalOrderStructure<E> ios)
	{
		return new BoundedIntervalImpl<>(lower, upper, ios);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!(obj instanceof BoundedIntervalImpl<?> other)) return false;
		return Objects.equals(lower, other.lower) && Objects.equals(upper, other.upper);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(lower, upper);
	}

	@Override
	public boolean isPoint()
	{
		return lower.isClosed() && upper.isClosed() && ios.compare(lower.value(), upper.value()).isEqualTo();
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other)
	{
		Bound<E> newLower = ios.tightestLowerBound(lower, other.lower());
		Bound<E> newUpper = ios.tightestUpperBound(upper, other.upper());
		return ios.isNonEmpty(newLower, newUpper)
				? Optional.of(new BoundedIntervalImpl<>(newLower, newUpper, ios))
				: Optional.empty();
	}

	@Override
	public boolean contains(final E element)
	{
		return ios.boundHarboursElementFromBelow(lower, element) && ios.boundHarboursElementFromAbove(upper, element);
	}

	@Override
	public boolean contains(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b ->
					ios.enclosesLowerBound(lower, b.lower()) && ios.enclosesUpperBound(upper, b.upper());
			case UnboundedInterval<E> ignored -> false;
		};
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b ->
					!ios.endPrecedesStart(upper, b.lower()) && !ios.endPrecedesStart(b.upper(), lower);
			case UnboundedInterval<E> u ->
			{
				boolean beforeUnbounded = u.lower().isPresent() && ios.endPrecedesStart(upper, u.lower().get());
				boolean afterUnbounded = u.upper().isPresent() && ios.endPrecedesStart(u.upper().get(), lower);
				yield !beforeUnbounded && !afterUnbounded;
			}
		};
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b -> ios.touchesAt(upper, b.lower()) || ios.touchesAt(b.upper(), lower);
			case UnboundedInterval<E> u -> u.lower().map(l -> ios.touchesAt(upper, l)).orElse(false)
					|| u.upper().map(u2 -> ios.touchesAt(u2, lower)).orElse(false);
		};
	}

	@Override
	public Interval<E> span(final Interval<E> other)
	{
		return switch (other)
		{
			case BoundedInterval<E> b -> BoundedIntervalImpl.of(ios.loosestLowerBound(lower, b.lower()),
					ios.loosestUpperBound(upper, b.upper()), ios);
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = u.lower().map(l -> ios.loosestLowerBound(lower, l));
				Optional<Bound<E>> newUpper = u.upper().map(u2 -> ios.loosestUpperBound(upper, u2));
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get(), ios)
						: UnboundedIntervalImpl.of(newLower, newUpper, ios);
			}
		};
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final UnboundedInterval<E> other)
	{
		Bound<E> newLower = other.lower().map(l -> ios.tightestLowerBound(lower, l)).orElse(lower);
		Bound<E> newUpper = other.upper().map(u -> ios.tightestUpperBound(upper, u)).orElse(upper);
		return ios.isNonEmpty(newLower, newUpper)
				? Optional.of(BoundedIntervalImpl.of(newLower, newUpper, ios))
				: Optional.empty();
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

	BoundedInterval<E> withBounds(final Bound<E> newLower, final Bound<E> newUpper)
	{
		return new BoundedIntervalImpl<>(newLower, newUpper, ios);
	}
}
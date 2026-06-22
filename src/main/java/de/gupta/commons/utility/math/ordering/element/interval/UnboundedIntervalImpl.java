package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.ordering.bound.AlgebraicBounds;
import de.gupta.commons.utility.math.ordering.bound.Bound;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.structure.IntervalOrderStructure;

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
		var ios = ios();
		return lower.map(b -> ios.boundHarboursElementFromBelow(b, element)).orElse(true)
				&& upper.map(b -> ios.boundHarboursElementFromAbove(b, element)).orElse(true);
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> b.overlaps(this);
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
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b -> b.abuts(this);
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
		var ios = ios();
		return switch (other)
		{
			case BoundedIntervalImpl<E> b ->
			{
				Optional<Bound<E>> newLower = lower.map(l -> ios.loosestLowerBound(l, b.lower()));
				Optional<Bound<E>> newUpper = upper.map(u -> ios.loosestUpperBound(u, b.upper()));
				yield of(newLower, newUpper);
			}
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = lower.isPresent() && u.lower().isPresent()
						? Optional.of(ios.loosestLowerBound(lower.get(), u.lower().get())) : Optional.empty();
				Optional<Bound<E>> newUpper = upper.isPresent() && u.upper().isPresent()
						? Optional.of(ios.loosestUpperBound(upper.get(), u.upper().get())) : Optional.empty();
				yield newLower.isPresent() && newUpper.isPresent()
						? BoundedIntervalImpl.of(newLower.get(), newUpper.get())
						: of(newLower, newUpper);
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

	@Override
	public boolean contains(final Interval<E> other)
	{
		return lower.map(l -> AlgebraicBounds.subsumesFromBelow(l, other.lowerBound())).orElse(true)
				&& upper.map(u -> AlgebraicBounds.subsumesFromAbove(u, other.upperBound())).orElse(true);
	}

	@Override
	public Optional<Interval<E>> intersect(final Interval<E> other)
	{
		var ios = ios();
		return switch (other)
		{
			case BoundedInterval<E> b -> intersect(b).map(i -> (Interval<E>) i);
			case UnboundedInterval<E> u ->
			{
				Optional<Bound<E>> newLower = lower.isPresent() && u.lower().isPresent()
						? Optional.of(ios.tightestLowerBound(lower.get(), u.lower().get()))
						: lower.or(u::lower);
				Optional<Bound<E>> newUpper = upper.isPresent() && u.upper().isPresent()
						? Optional.of(ios.tightestUpperBound(upper.get(), u.upper().get()))
						: upper.or(u::upper);

				if (newLower.isPresent() && newUpper.isPresent())
				{
					yield ios.isNonEmpty(newLower.get(), newUpper.get())
							? Optional.of(BoundedIntervalImpl.of(newLower.get(), newUpper.get()))
							: Optional.empty();
				}

				yield Optional.of(of(newLower, newUpper));
			}
		};
	}

	@Override
	public Optional<BoundedInterval<E>> intersect(final BoundedInterval<E> other)
	{
		var ios = ios();
		Bound<E> newLower = lower.map(l -> ios.tightestLowerBound(l, other.lower())).orElse(other.lower());
		Bound<E> newUpper = upper.map(u -> ios.tightestUpperBound(u, other.upper())).orElse(other.upper());
		return ios.isNonEmpty(newLower, newUpper) ? Optional.of(BoundedIntervalImpl.of(newLower, newUpper))
				: Optional.empty();
	}

	private IntervalOrderStructure<E> ios()
	{
		return IntervalOrderStructure.forElements();
	}

	UnboundedIntervalImpl
	{
		if (lower.isPresent() && upper.isPresent())
			throw new IllegalArgumentException("Use BoundedInterval when both bounds are present.");
	}
}
package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public record EmptyInterval<E extends TotallyOrdered<E>>() implements Interval<E>
{
	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	public boolean isPoint()
	{
		return false;
	}

	@Override
	public boolean contains(final E element)
	{
		return false;
	}

	@Override
	public boolean contains(final Interval<E> other)
	{
		return other.isEmpty();
	}

	@Override
	public boolean overlaps(final Interval<E> other)
	{
		return false;
	}

	@Override
	public Interval<E> intersect(final Interval<E> other)
	{
		return this;
	}

	@Override
	public boolean abuts(final Interval<E> other)
	{
		return false;
	}

	@Override
	public Optional<Bound<E>> lowerBound()
	{
		return Optional.empty();
	}

	@Override
	public Optional<Bound<E>> upperBound()
	{
		return Optional.empty();
	}
}

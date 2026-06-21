package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.Bound;

import java.util.Optional;

public record EmptyInterval<E>() implements Interval<E>
{
	@Override
	public boolean isEmpty()
	{
		return true;
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

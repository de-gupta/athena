package de.gupta.commons.utility.math.interpolation.data;

import de.gupta.aletheia.collection.Dyad;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public sealed interface InterpolationData<X, Y> permits SortedInterpolationData
{
	static <X, Y> InterpolationData<X, Y> of(final List<Sample<X, Y>> samples, final Comparator<X> order)
	{
		return new SortedInterpolationData<>(samples, order);
	}

	List<Sample<X, Y>> samples();

	Optional<Dyad<Sample<X, Y>, Sample<X, Y>>> bracket(X query);
}

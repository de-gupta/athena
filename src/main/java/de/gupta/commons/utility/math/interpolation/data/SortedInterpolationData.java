package de.gupta.commons.utility.math.interpolation.data;

import de.gupta.aletheia.collection.Dyad;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class SortedInterpolationData<X, Y> implements InterpolationData<X, Y>
{
	private final List<Sample<X, Y>> samples;
	private final Comparator<X> order;

	@Override
	public List<Sample<X, Y>> samples()
	{
		return samples;
	}

	@Override
	public Optional<Dyad<Sample<X, Y>, Sample<X, Y>>> bracket(final X query)
	{
		Objects.requireNonNull(query, "query");
		int size = samples.size();
		if (size < 2)
		{
			return Optional.empty();
		}
		int low = 0;
		int high = size - 1;
		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			int comparison = order.compare(samples.get(mid).x(), query);
			if (comparison < 0)
			{
				low = mid + 1;
			}
			else if (comparison > 0)
			{
				high = mid - 1;
			}
			else
			{
				int leftIndex = Math.min(mid, size - 2);
				return Optional.of(Dyad.of(samples.get(leftIndex), samples.get(leftIndex + 1)));
			}
		}
//		int leftIndex = Math.max(0, Math.min(low - 1, size - 2));
		int leftIndex = Math.clamp(low - 1, 0, size - 2);
		return Optional.of(Dyad.of(samples.get(leftIndex), samples.get(leftIndex + 1)));
	}

	SortedInterpolationData(final List<Sample<X, Y>> samples, final Comparator<X> order)
	{
		Objects.requireNonNull(samples, "samples");
		Objects.requireNonNull(order, "order");
		samples.forEach(sample -> Objects.requireNonNull(sample, "sample"));
		this.order = order;
		this.samples = samples.stream()
		                      .sorted(Comparator.comparing(Sample::x, order))
		                      .toList();
	}
}
package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;
import de.gupta.commons.utility.math.analysis.space.MetricSpace;
import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.TieBreakingPolicy;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;

import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;

public final class NearestNeighborMethod<X, Y> implements InterpolationMethod<X, Y>
{
	private final MetricSpace<X> space;
	private final TieBreakingPolicy tieBreaking;
	private final BinaryOperator<Y> averageFunction;

	public static <X, Y> NearestNeighborMethod<X, Y> of(
			final MetricSpace<X> space,
			final TieBreakingPolicy tieBreaking)
	{
		Objects.requireNonNull(space, "space");
		Objects.requireNonNull(tieBreaking, "tieBreaking");
		if (tieBreaking == TieBreakingPolicy.AVERAGE)
		{
			throw new IllegalArgumentException(
					"AVERAGE tie-breaking requires a vector space; use NearestNeighborMethod.averaging()");
		}
		return new NearestNeighborMethod<>(space, tieBreaking, null);
	}

	public static <X, Y, F> NearestNeighborMethod<X, Y> averaging(
			final MetricSpace<X> space,
			final VectorSpaceStructure<Y, F> outputSpace)
	{
		Objects.requireNonNull(space, "space");
		Objects.requireNonNull(outputSpace, "outputSpace");
		F two = outputSpace.scalars().add(outputSpace.scalars().one(), outputSpace.scalars().one());
		F half = outputSpace.scalars().multiplicativeInverse(two);
		BinaryOperator<Y> average = (a, b) -> outputSpace.scale(half, outputSpace.add(a, b));
		return new NearestNeighborMethod<>(space, TieBreakingPolicy.AVERAGE, average);
	}

	@Override
	public Interpolator<X, Y> fit(final InterpolationData<X, Y> data)
	{
		Objects.requireNonNull(data, "data");
		List<Sample<X, Y>> samples = data.samples();
		if (samples.isEmpty())
		{
			throw new IllegalArgumentException("nearest-neighbor requires at least one sample");
		}
		return query -> interpolate(samples, query);
	}

	private Y interpolate(final List<Sample<X, Y>> samples, final X query)
	{
		double minimumDistance = samples.stream()
		                                .mapToDouble(sample -> space.distance(query, sample.x()))
		                                .min()
		                                .orElseThrow();

		List<Sample<X, Y>> nearest = samples.stream()
		                                    .filter(sample -> space.distance(query, sample.x()) == minimumDistance)
		                                    .toList();

		return switch (tieBreaking)
		{
			case LOWER -> nearest.getFirst().y();
			case UPPER -> nearest.getLast().y();
			case AVERAGE -> averageFunction.apply(nearest.getFirst().y(), nearest.getLast().y());
		};
	}

	private NearestNeighborMethod(
			final MetricSpace<X> space,
			final TieBreakingPolicy tieBreaking,
			final BinaryOperator<Y> averageFunction)
	{
		this.space = space;
		this.tieBreaking = tieBreaking;
		this.averageFunction = averageFunction;
	}
}

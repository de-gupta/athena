package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.aletheia.collection.Dyad;
import de.gupta.commons.utility.math.algebra.structure.module.ModuleStructure;
import de.gupta.commons.utility.math.analysis.space.InterpolatableSpace;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;

import java.util.List;
import java.util.Objects;
import java.util.function.DoubleFunction;

public final class LinearInterpolationMethod<X, Y, F> implements InterpolationMethod<X, Y>
{
	private final InterpolatableSpace<X> space;
	private final ModuleStructure<Y, F> outputSpace;
	private final DoubleFunction<F> scalarOf;
	private final ExtrapolationPolicy extrapolation;

	public static <X, Y, F> LinearInterpolationMethod<X, Y, F> of(
			final InterpolatableSpace<X> space,
			final ModuleStructure<Y, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ExtrapolationPolicy extrapolation)
	{
		Objects.requireNonNull(space, "space");
		Objects.requireNonNull(outputSpace, "outputSpace");
		Objects.requireNonNull(scalarOf, "scalarOf");
		Objects.requireNonNull(extrapolation, "extrapolation");
		return new LinearInterpolationMethod<>(space, outputSpace, scalarOf, extrapolation);
	}

	@Override
	public Interpolator<X, Y> fit(final InterpolationData<X, Y> data)
	{
		Objects.requireNonNull(data, "data");
		if (data.samples().size() < 2)
		{
			throw new IllegalArgumentException("linear interpolation requires at least two samples");
		}
		return query -> interpolate(data, query);
	}

	private Y interpolate(final InterpolationData<X, Y> data, final X query)
	{
		List<Sample<X, Y>> samples = data.samples();
		boolean belowMinimum = space.compare(query, samples.getFirst().x()) < 0;
		boolean aboveMaximum = space.compare(query, samples.getLast().x()) > 0;

		if ((belowMinimum || aboveMaximum) && extrapolation == ExtrapolationPolicy.FORBIDDEN)
		{
			throw new IllegalArgumentException("query is out of interpolation range: " + query);
		}
		if (belowMinimum && extrapolation == ExtrapolationPolicy.FLAT)
		{
			return samples.getFirst().y();
		}
		if (aboveMaximum && extrapolation == ExtrapolationPolicy.FLAT)
		{
			return samples.getLast().y();
		}
		return blend(data.bracket(query).orElseThrow(), query);
	}

	private Y blend(final Dyad<Sample<X, Y>, Sample<X, Y>> bracket, final X query)
	{
		double lambda = space.parameter(bracket.sinister().x(), bracket.dexter().x(), query);
		F lambdaF = scalarOf.apply(lambda);
		F oneMinusLambdaF = outputSpace.scalars().subtract(outputSpace.scalars().one(), lambdaF);
		return outputSpace.add(
				outputSpace.scale(oneMinusLambdaF, bracket.sinister().y()),
				outputSpace.scale(lambdaF, bracket.dexter().y())
		);
	}

	private LinearInterpolationMethod(
			final InterpolatableSpace<X> space,
			final ModuleStructure<Y, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ExtrapolationPolicy extrapolation)
	{
		this.space = space;
		this.outputSpace = outputSpace;
		this.scalarOf = scalarOf;
		this.extrapolation = extrapolation;
	}
}
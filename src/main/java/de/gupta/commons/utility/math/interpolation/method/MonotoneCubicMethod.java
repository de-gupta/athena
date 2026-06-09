package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;
import de.gupta.commons.utility.math.analysis.space.DifferentiableSpace;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;

import java.util.List;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;

public final class MonotoneCubicMethod<X, F> implements InterpolationMethod<X, F>
{
	private final DifferentiableSpace<X> space;
	private final VectorSpaceStructure<F, F> outputSpace;
	private final DoubleFunction<F> scalarOf;
	private final ToDoubleFunction<F> doubleOf;
	private final ExtrapolationPolicy extrapolation;

	public static <X, F> MonotoneCubicMethod<X, F> of(
			final DifferentiableSpace<X> space,
			final VectorSpaceStructure<F, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ToDoubleFunction<F> doubleOf,
			final ExtrapolationPolicy extrapolation)
	{
		Objects.requireNonNull(space, "space");
		Objects.requireNonNull(outputSpace, "outputSpace");
		Objects.requireNonNull(scalarOf, "scalarOf");
		Objects.requireNonNull(doubleOf, "doubleOf");
		Objects.requireNonNull(extrapolation, "extrapolation");
		return new MonotoneCubicMethod<>(space, outputSpace, scalarOf, doubleOf, extrapolation);
	}

	@Override
	public Interpolator<X, F> fit(final InterpolationData<X, F> data)
	{
		Objects.requireNonNull(data, "data");
		List<Sample<X, F>> samples = data.samples();
		if (samples.size() < 2)
		{
			throw new IllegalArgumentException("monotone cubic interpolation requires at least two samples");
		}
		int intervalCount = samples.size() - 1;
		double[] spans = computeSpans(samples, intervalCount);
		List<F> tangents = computeTangents(samples, spans, intervalCount);
		List<X> xValues = samples.stream().map(Sample::x).toList();
		return query -> evaluate(samples, xValues, spans, tangents, query);
	}

	private double[] computeSpans(final List<Sample<X, F>> samples, final int intervalCount)
	{
		double[] spans = new double[intervalCount];
		for (int i = 0; i < intervalCount; i++)
		{
			spans[i] = space.span(samples.get(i).x(), samples.get(i + 1).x());
		}
		return spans;
	}

	private List<F> computeTangents(
			final List<Sample<X, F>> samples,
			final double[] spans,
			final int intervalCount)
	{
		F[] slopes = allocate(intervalCount);
		for (int i = 0; i < intervalCount; i++)
		{
			slopes[i] = outputSpace.scalars().divide(
					outputSpace.subtract(samples.get(i + 1).y(), samples.get(i).y()),
					scalarOf.apply(spans[i])
			);
		}

		F[] tangents = allocate(intervalCount + 1);
		tangents[0] = slopes[0];
		for (int i = 1; i < intervalCount; i++)
		{
			tangents[i] = outputSpace.scalars().divide(
					outputSpace.scalars().add(slopes[i - 1], slopes[i]),
					scalarOf.apply(2.0)
			);
		}
		tangents[intervalCount] = slopes[intervalCount - 1];

		for (int i = 0; i < intervalCount; i++)
		{
			double slopeAsDouble = doubleOf.applyAsDouble(slopes[i]);
			if (slopeAsDouble == 0.0)
			{
				tangents[i] = outputSpace.zero();
				tangents[i + 1] = outputSpace.zero();
			}
			else
			{
				double alpha = doubleOf.applyAsDouble(tangents[i]) / slopeAsDouble;
				double beta = doubleOf.applyAsDouble(tangents[i + 1]) / slopeAsDouble;
				double squaredNorm = alpha * alpha + beta * beta;
				if (squaredNorm > 9.0)
				{
					double tau = 3.0 / Math.sqrt(squaredNorm);
					tangents[i] = outputSpace.scalars().multiply(scalarOf.apply(tau * alpha), slopes[i]);
					tangents[i + 1] = outputSpace.scalars().multiply(scalarOf.apply(tau * beta), slopes[i]);
				}
			}
		}

		return List.of(tangents);
	}

	private F evaluate(
			final List<Sample<X, F>> samples,
			final List<X> xValues,
			final double[] spans,
			final List<F> tangents,
			final X query)
	{
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
		return evaluateHermite(samples, xValues, spans, tangents, query);
	}

	private F evaluateHermite(
			final List<Sample<X, F>> samples,
			final List<X> xValues,
			final double[] spans,
			final List<F> tangents,
			final X query)
	{
		int i = findIntervalIndex(xValues, query);
		double span = spans[i];
		double lambda = space.parameter(samples.get(i).x(), samples.get(i + 1).x(), query);

		double h00 = (1 + 2 * lambda) * (1 - lambda) * (1 - lambda);
		double h10 = lambda * (1 - lambda) * (1 - lambda);
		double h01 = lambda * lambda * (3 - 2 * lambda);
		double h11 = lambda * lambda * (lambda - 1);

		return outputSpace.add(
				outputSpace.add(
						outputSpace.scalars().multiply(scalarOf.apply(h00), samples.get(i).y()),
						outputSpace.scalars().multiply(scalarOf.apply(span * h10), tangents.get(i))
				),
				outputSpace.add(
						outputSpace.scalars().multiply(scalarOf.apply(h01), samples.get(i + 1).y()),
						outputSpace.scalars().multiply(scalarOf.apply(span * h11), tangents.get(i + 1))
				)
		);
	}

	private int findIntervalIndex(final List<X> xValues, final X query)
	{
		int low = 0;
		int high = xValues.size() - 1;
		while (low <= high)
		{
			int midpoint = (low + high) >>> 1;
			int comparison = space.compare(xValues.get(midpoint), query);
			if (comparison < 0)
			{
				low = midpoint + 1;
			}
			else if (comparison > 0)
			{
				high = midpoint - 1;
			}
			else
			{
				return Math.min(midpoint, xValues.size() - 2);
			}
		}
//		return Math.max(0, Math.min(low - 1, xValues.size() - 2));
		return Math.clamp(low - 1, 0, xValues.size() - 2);
	}

	@SuppressWarnings("unchecked")
	private F[] allocate(final int size)
	{
		return (F[]) new Object[size];
	}

	private MonotoneCubicMethod(
			final DifferentiableSpace<X> space,
			final VectorSpaceStructure<F, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ToDoubleFunction<F> doubleOf,
			final ExtrapolationPolicy extrapolation)
	{
		this.space = space;
		this.outputSpace = outputSpace;
		this.scalarOf = scalarOf;
		this.doubleOf = doubleOf;
		this.extrapolation = extrapolation;
	}
}
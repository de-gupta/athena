package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.algebra.structure.module.VectorSpaceStructure;
import de.gupta.commons.utility.math.analysis.space.DifferentiableSpace;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleFunction;

public final class NaturalCubicSplineMethod<X, Y, F> implements InterpolationMethod<X, Y>
{
	private final DifferentiableSpace<X> space;
	private final VectorSpaceStructure<Y, F> outputSpace;
	private final DoubleFunction<F> scalarOf;
	private final ExtrapolationPolicy extrapolation;

	public static <X, Y, F> NaturalCubicSplineMethod<X, Y, F> of(
			final DifferentiableSpace<X> space,
			final VectorSpaceStructure<Y, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ExtrapolationPolicy extrapolation)
	{
		Objects.requireNonNull(space, "space");
		Objects.requireNonNull(outputSpace, "outputSpace");
		Objects.requireNonNull(scalarOf, "scalarOf");
		Objects.requireNonNull(extrapolation, "extrapolation");
		return new NaturalCubicSplineMethod<>(space, outputSpace, scalarOf, extrapolation);
	}

	@Override
	public Interpolator<X, Y> fit(final InterpolationData<X, Y> data)
	{
		Objects.requireNonNull(data, "data");
		List<Sample<X, Y>> samples = data.samples();
		if (samples.size() < 2)
		{
			throw new IllegalArgumentException("cubic spline requires at least two samples");
		}
		int intervalCount = samples.size() - 1;
		double[] spans = computeSpans(samples, intervalCount);
		List<Y> secondDerivatives = computeSecondDerivatives(samples, spans, intervalCount);
		List<X> xValues = samples.stream().map(Sample::x).toList();
		return query -> evaluate(samples, xValues, spans, secondDerivatives, query);
	}

	private double[] computeSpans(final List<Sample<X, Y>> samples, final int intervalCount)
	{
		double[] spans = new double[intervalCount];
		for (int i = 0; i < intervalCount; i++)
		{
			spans[i] = space.span(samples.get(i).x(), samples.get(i + 1).x());
		}
		return spans;
	}

	private List<Y> computeSecondDerivatives(
			final List<Sample<X, Y>> samples,
			final double[] spans,
			final int intervalCount)
	{
		int interiorCount = intervalCount - 1;
		if (interiorCount == 0)
		{
			return List.of(outputSpace.zero(), outputSpace.zero());
		}

		double[] mainDiagonal = new double[interiorCount];
		double[] offDiagonal = new double[interiorCount - 1];
		List<Y> rightHandSide = new ArrayList<>(interiorCount);

		for (int j = 0; j < interiorCount; j++)
		{
			mainDiagonal[j] = 2.0 * (spans[j] + spans[j + 1]);
		}
		System.arraycopy(spans, 1, offDiagonal, 0, interiorCount - 1);
		for (int j = 0; j < interiorCount; j++)
		{
			int i = j + 1;
			Y secondDividedDifference = outputSpace.add(
					outputSpace.add(
							outputSpace.scale(scalarOf.apply(1.0 / spans[i]), samples.get(i + 1).y()),
							outputSpace.scale(scalarOf.apply(-(1.0 / spans[i] + 1.0 / spans[i - 1])),
									samples.get(i).y())
					),
					outputSpace.scale(scalarOf.apply(1.0 / spans[i - 1]), samples.get(i - 1).y())
			);
			rightHandSide.add(outputSpace.scale(scalarOf.apply(6.0), secondDividedDifference));
		}

		List<Y> interior = TridiagonalSolver.solve(offDiagonal, mainDiagonal, offDiagonal, rightHandSide,
				outputSpace, scalarOf);

		List<Y> allSecondDerivatives = new ArrayList<>(intervalCount + 1);
		allSecondDerivatives.add(outputSpace.zero());
		allSecondDerivatives.addAll(interior);
		allSecondDerivatives.add(outputSpace.zero());
		return allSecondDerivatives;
	}

	private Y evaluate(
			final List<Sample<X, Y>> samples,
			final List<X> xValues,
			final double[] spans,
			final List<Y> secondDerivatives,
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
		return evaluateSpline(samples, xValues, spans, secondDerivatives, query);
	}

	private Y evaluateSpline(
			final List<Sample<X, Y>> samples,
			final List<X> xValues,
			final double[] spans,
			final List<Y> secondDerivatives,
			final X query)
	{
		int i = findIntervalIndex(xValues, query);
		double span = spans[i];
		double lambda = space.parameter(samples.get(i).x(), samples.get(i + 1).x(), query);
		double spanSquaredOverSix = span * span / 6.0;
		double coefficientLeft = spanSquaredOverSix * (1 - lambda) * lambda * (lambda - 2);
		double coefficientRight = spanSquaredOverSix * lambda * (lambda - 1) * (lambda + 1);

		return outputSpace.add(
				outputSpace.add(
						outputSpace.scale(scalarOf.apply(1 - lambda), samples.get(i).y()),
						outputSpace.scale(scalarOf.apply(lambda), samples.get(i + 1).y())
				),
				outputSpace.add(
						outputSpace.scale(scalarOf.apply(coefficientLeft), secondDerivatives.get(i)),
						outputSpace.scale(scalarOf.apply(coefficientRight), secondDerivatives.get(i + 1))
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
		return Math.clamp(low - 1, 0, xValues.size() - 2);
	}

	private NaturalCubicSplineMethod(
			final DifferentiableSpace<X> space,
			final VectorSpaceStructure<Y, F> outputSpace,
			final DoubleFunction<F> scalarOf,
			final ExtrapolationPolicy extrapolation)
	{
		this.space = space;
		this.outputSpace = outputSpace;
		this.scalarOf = scalarOf;
		this.extrapolation = extrapolation;
	}
}
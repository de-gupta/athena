package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.analysis.space.standard.DoubleInterpolatableSpace;
import de.gupta.commons.utility.math.analysis.space.standard.DoubleVectorSpaceStructure;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("NaturalCubicSplineMethod")
final class NaturalCubicSplineMethodTest
{
	private static final NaturalCubicSplineMethod<Double, Double, Double> FLAT_METHOD =
			NaturalCubicSplineMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
					d -> d, ExtrapolationPolicy.FLAT);

	private static InterpolationData<Double, Double> samplesOf(final double... pairs)
	{
		List<Sample<Double, Double>> samples = java.util.stream.IntStream.iterate(0, i -> i + 2)
		                                                                 .limit(pairs.length / 2)
		                                                                 .mapToObj(i -> new Sample<>(pairs[i],
																				 pairs[i + 1]))
		                                                                 .toList();
		return InterpolationData.of(samples, Double::compare);
	}

	@Nested
	@DisplayName("when samples lie on a linear function")
	final class WhenSamplesLieOnLinearFunction
	{
		@Test
		@DisplayName("reproduces the linear function exactly at any interior query")
		void reproducesLinearFunctionExactly()
		{
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0);
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(data);

			assertThat(interpolator.interpolate(0.5))
					.as("spline through linear points must reproduce the line at x=0.5")
					.isCloseTo(0.5, within(1e-12));
			assertThat(interpolator.interpolate(1.5))
					.as("spline through linear points must reproduce the line at x=1.5")
					.isCloseTo(1.5, within(1e-12));
			assertThat(interpolator.interpolate(2.5))
					.as("spline through linear points must reproduce the line at x=2.5")
					.isCloseTo(2.5, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when evaluated at a known reference point")
	final class WhenEvaluatedAtKnownReferencePoint
	{
		@Test
		@DisplayName("returns the analytically computed value for 3 quadratic samples")
		void returnsAnalyticallyComputedValueForQuadraticSamples()
		{
			// Samples (0,0),(1,1),(2,4): h₀=h₁=1, M₀=M₂=0
			// Interior eq: 4*M₁ = 6*((4-1)/1 - (1-0)/1) = 12  →  M₁=3
			// S(0.5) in interval [0,1], λ=0.5:
			//   h²/6 = 1/6, coeffLeft = 1/6*(0.5*0.5*(0.5-2)) = -0.0625
			//   S = 0*0.5 + 1*0.5 + (-0.0625)*0 + (-0.0625)*3 = 0.5 - 0.1875 = 0.3125
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 1.0, 2.0, 4.0);

			assertThat(FLAT_METHOD.fit(data).interpolate(0.5))
					.as("natural spline through (0,0),(1,1),(2,4) must give 0.3125 at x=0.5")
					.isCloseTo(0.3125, within(1e-12));
		}

		@Test
		@DisplayName("returns exact knot value when query matches a knot")
		void returnsExactKnotValueOnExactHit()
		{
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 1.0, 2.0, 4.0);

			assertThat(FLAT_METHOD.fit(data).interpolate(1.0))
					.as("query at knot must return exact knot value")
					.isCloseTo(1.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when query is out of range")
	final class WhenQueryIsOutOfRange
	{
		@Test
		@DisplayName("FLAT policy clamps to boundary value below minimum")
		void flatClampsToLeftBoundary()
		{
			assertThat(FLAT_METHOD.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(0.0))
					.as("FLAT: below minimum must return leftmost y")
					.isCloseTo(10.0, within(1e-12));
		}

		@Test
		@DisplayName("FLAT policy clamps to boundary value above maximum")
		void flatClampsToRightBoundary()
		{
			assertThat(FLAT_METHOD.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(99.0))
					.as("FLAT: above maximum must return rightmost y")
					.isCloseTo(20.0, within(1e-12));
		}

		@Test
		@DisplayName("LINEAR policy extends the boundary cubic below minimum")
		void linearExtendsBelowMinimum()
		{
			NaturalCubicSplineMethod<Double, Double, Double> method =
					NaturalCubicSplineMethod.of(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.LINEAR);

			double belowMinResult = method.fit(samplesOf(0.0, 0.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0)).interpolate(-1.0);

			assertThat(belowMinResult)
					.as("LINEAR: extrapolating a linear spline left must give -1.0")
					.isCloseTo(-1.0, within(1e-12));
		}

		@Test
		@DisplayName("FORBIDDEN policy throws below minimum")
		void forbiddenThrowsBelowMinimum()
		{
			NaturalCubicSplineMethod<Double, Double, Double> method =
					NaturalCubicSplineMethod.of(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.FORBIDDEN);

			assertThatIllegalArgumentException()
					.as("FORBIDDEN: query below minimum must throw")
					.isThrownBy(() -> method.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(0.0));
		}

		@Test
		@DisplayName("FORBIDDEN policy throws above maximum")
		void forbiddenThrowsAboveMaximum()
		{
			NaturalCubicSplineMethod<Double, Double, Double> method =
					NaturalCubicSplineMethod.of(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.FORBIDDEN);

			assertThatIllegalArgumentException()
					.as("FORBIDDEN: query above maximum must throw")
					.isThrownBy(() -> method.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(99.0));
		}
	}

	@Nested
	@DisplayName("with invalid data")
	final class WithInvalidData
	{
		@Test
		@DisplayName("fit() throws on fewer than two samples")
		void fitThrowsOnSingleSample()
		{
			assertThatIllegalArgumentException()
					.as("fit() must throw when fewer than two samples provided")
					.isThrownBy(() -> FLAT_METHOD.fit(samplesOf(1.0, 1.0)));
		}

		@Test
		@DisplayName("fit() throws NullPointerException on null data")
		void fitThrowsOnNullData()
		{
			assertThatNullPointerException()
					.as("fit() must throw NullPointerException on null data")
					.isThrownBy(() -> FLAT_METHOD.fit(null))
					.withMessage("data");
		}
	}
}
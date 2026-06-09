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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MonotoneCubicMethod#fit")
final class MonotoneCubicMethodTest
{
	private static final MonotoneCubicMethod<Double, Double> FLAT_METHOD =
			MonotoneCubicMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
					d -> d, f -> f, ExtrapolationPolicy.FLAT);

	private static InterpolationData<Double, Double> samplesOf(final double... pairs)
	{
		List<Sample<Double, Double>> samples = IntStream.iterate(0, i -> i + 2)
		                                                .limit(pairs.length / 2)
		                                                .mapToObj(i -> new Sample<>(pairs[i], pairs[i + 1]))
		                                                .toList();
		return InterpolationData.of(samples, Double::compare);
	}

	@Nested
	@DisplayName("when samples lie on a linear function")
	final class WhenSamplesLieOnLinearFunction
	{
		@Test
		@DisplayName("reproduces the linear function exactly")
		void reproducesLinearFunctionExactly()
		{
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 1.0, 2.0, 2.0, 3.0, 3.0);
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(data);

			assertThat(interpolator.interpolate(1.5))
					.as("monotone cubic through linear points must reproduce the line at x=1.5")
					.isCloseTo(1.5, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when input is strictly monotone")
	final class WhenInputIsStrictlyMonotone
	{
		@Test
		@DisplayName("interpolated values do not overshoot between any two adjacent knots")
		void doesNotOvershootBetweenAdjacentKnots()
		{
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 0.0, 2.0, 1.0, 3.0, 1.0);
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(data);

			for (int tick = 0; tick <= 100; tick++)
			{
				double x = tick / 100.0 * 3.0;
				double result = interpolator.interpolate(x);
				assertThat(result)
						.as("monotone cubic must not overshoot below 0 or above 1 at x=" + x)
						.isBetween(-1e-12, 1.0 + 1e-12);
			}
		}

		@Test
		@DisplayName("interpolated values stay within adjacent knot values on flat sections")
		void staysWithinBoundsOnFlatSections()
		{
			InterpolationData<Double, Double> data = samplesOf(0.0, 0.0, 1.0, 0.0, 2.0, 1.0, 3.0, 1.0);
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(data);

			for (int tick = 0; tick <= 100; tick++)
			{
				double x = tick / 100.0;
				assertThat(interpolator.interpolate(x))
						.as("flat section [0,1] must stay in [0, 0] at x=" + x)
						.isBetween(-1e-12, 1e-12);
			}
		}
	}

	@Nested
	@DisplayName("when query is out of range")
	final class WhenQueryIsOutOfRange
	{
		@Test
		@DisplayName("FLAT policy clamps to boundary value")
		void flatClampsToLeftBoundary()
		{
			assertThat(FLAT_METHOD.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(0.0))
					.as("FLAT: below minimum must return leftmost y")
					.isCloseTo(10.0, within(1e-12));
		}

		@Test
		@DisplayName("FORBIDDEN policy throws for out-of-range query")
		void forbiddenThrowsOutOfRange()
		{
			MonotoneCubicMethod<Double, Double> method =
					MonotoneCubicMethod.of(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, f -> f, ExtrapolationPolicy.FORBIDDEN);

			assertThatIllegalArgumentException()
					.as("FORBIDDEN: out-of-range query must throw")
					.isThrownBy(() -> method.fit(samplesOf(1.0, 10.0, 2.0, 20.0)).interpolate(0.0));
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
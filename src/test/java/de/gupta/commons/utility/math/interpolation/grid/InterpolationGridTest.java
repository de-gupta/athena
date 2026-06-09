package de.gupta.commons.utility.math.interpolation.grid;

import de.gupta.commons.utility.math.analysis.space.standard.DoubleInterpolatableSpace;
import de.gupta.commons.utility.math.analysis.space.standard.DoubleVectorSpaceStructure;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.InterpolationMethod;
import de.gupta.commons.utility.math.interpolation.data.GridSample;
import de.gupta.commons.utility.math.interpolation.method.LinearInterpolationMethod;
import de.gupta.commons.utility.math.interpolation.method.MonotoneCubicMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("InterpolationGrid")
final class InterpolationGridTest
{
	private static final InterpolationMethod<Double, Double> LINEAR_FLAT =
			LinearInterpolationMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
					d -> d, ExtrapolationPolicy.FLAT);

	private static final InterpolationMethod<Double, Double> MONOTONE_FLAT =
			MonotoneCubicMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
					d -> d, f -> f, ExtrapolationPolicy.FLAT);

	private static List<GridSample<Double, Double, Double>> linearGrid()
	{
		return List.of(
				new GridSample<>(0.0, 0.0, 0.0), new GridSample<>(0.0, 1.0, 1.0), new GridSample<>(0.0, 2.0, 2.0),
				new GridSample<>(1.0, 0.0, 1.0), new GridSample<>(1.0, 1.0, 2.0), new GridSample<>(1.0, 2.0, 3.0),
				new GridSample<>(2.0, 0.0, 2.0), new GridSample<>(2.0, 1.0, 3.0), new GridSample<>(2.0, 2.0, 4.0)
		);
	}

	private static InterpolationGrid<Double, Double, Double> linearLinearGrid()
	{
		return InterpolationGrid.<Double, Double, Double>builder()
		                        .withData(linearGrid())
		                        .dimension1(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
		                        .dimension2(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
		                        .build();
	}

	@Nested
	@DisplayName("when queried at exact grid knots")
	final class WhenQueriedAtExactGridKnots
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsExactKnotValueCases")
		@DisplayName("returns the exact knot value")
		void returnsExactKnotValue(final String as, final double x1, final double x2, final double expected)
		{
			assertThat(linearLinearGrid().interpolate(x1, x2))
					.as(as)
					.isCloseTo(expected, within(1e-12));
		}

		private static Stream<Arguments> returnsExactKnotValueCases()
		{
			return Stream.of(
					Arguments.of("corner (0,0) → 0", 0.0, 0.0, 0.0),
					Arguments.of("corner (2,2) → 4", 2.0, 2.0, 4.0),
					Arguments.of("interior (1,1) → 2", 1.0, 1.0, 2.0),
					Arguments.of("edge (0,2) → 2", 0.0, 2.0, 2.0),
					Arguments.of("edge (2,0) → 2", 2.0, 0.0, 2.0)
			);
		}
	}

	@Nested
	@DisplayName("when queried at interior points")
	final class WhenQueriedAtInteriorPoints
	{
		@Test
		@DisplayName("returns bilinearly interpolated value for a linear grid")
		void returnsBilinearlyInterpolatedValue()
		{
			assertThat(linearLinearGrid().interpolate(0.5, 0.5))
					.as("midpoint of linear y=x1+x2 grid must give 1.0")
					.isCloseTo(1.0, within(1e-12));
		}

		@Test
		@DisplayName("returns correct value at asymmetric interior point")
		void returnsCorrectValueAtAsymmetricInteriorPoint()
		{
			assertThat(linearLinearGrid().interpolate(0.3, 1.7))
					.as("y=x1+x2=2.0 for (0.3, 1.7)")
					.isCloseTo(2.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when each dimension uses a different method")
	final class WhenDimensionsUseDifferentMethods
	{
		@Test
		@DisplayName("linear dim1 × monotone-cubic dim2 reproduces a linear surface")
		void linearTimesMonotoneCubicReproducesLinearSurface()
		{
			InterpolationGrid<Double, Double, Double> grid =
					InterpolationGrid.<Double, Double, Double>builder()
					                 .withData(linearGrid())
					                 .dimension1(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
					                 .dimension2(DoubleInterpolatableSpace.LINEAR, MONOTONE_FLAT)
					                 .build();

			assertThat(grid.interpolate(0.5, 0.5))
					.as("linear × monotone-cubic must reproduce y=x1+x2=1.0 at (0.5,0.5)")
					.isCloseTo(1.0, within(1e-12));
			assertThat(grid.interpolate(1.5, 0.75))
					.as("linear × monotone-cubic must reproduce y=x1+x2=2.25 at (1.5,0.75)")
					.isCloseTo(2.25, within(1e-12));
		}

		@Test
		@DisplayName("monotone-cubic dim1 × linear dim2 reproduces a linear surface")
		void monotoneCubicTimesLinearReproducesLinearSurface()
		{
			InterpolationGrid<Double, Double, Double> grid =
					InterpolationGrid.<Double, Double, Double>builder()
					                 .withData(linearGrid())
					                 .dimension1(DoubleInterpolatableSpace.LINEAR, MONOTONE_FLAT)
					                 .dimension2(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
					                 .build();

			assertThat(grid.interpolate(0.5, 1.5))
					.as("monotone-cubic × linear must reproduce y=x1+x2=2.0 at (0.5,1.5)")
					.isCloseTo(2.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when query is out of range")
	final class WhenQueryIsOutOfRange
	{
		@Test
		@DisplayName("FLAT dim2 policy clamps out-of-range X2 to boundary")
		void flatDim2ClampsToXTwoBoundary()
		{
			assertThat(linearLinearGrid().interpolate(1.0, 99.0))
					.as("FLAT dim2: x2=99 clamps to x2=2, y=x1+2=1+2=3")
					.isCloseTo(3.0, within(1e-12));
		}

		@Test
		@DisplayName("FLAT dim1 policy clamps out-of-range X1 to boundary")
		void flatDim1ClampsToXOneBoundary()
		{
			assertThat(linearLinearGrid().interpolate(-1.0, 1.0))
					.as("FLAT dim1: x1=-1 clamps to x1=0, y=0+1=1")
					.isCloseTo(1.0, within(1e-12));
		}

		@Test
		@DisplayName("FORBIDDEN dim2 policy throws for out-of-range X2")
		void forbiddenDim2ThrowsForOutOfRangeX2()
		{
			InterpolationMethod<Double, Double> forbiddenLinear =
					LinearInterpolationMethod.of(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.FORBIDDEN);

			InterpolationGrid<Double, Double, Double> grid =
					InterpolationGrid.<Double, Double, Double>builder()
					                 .withData(linearGrid())
					                 .dimension1(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
					                 .dimension2(DoubleInterpolatableSpace.LINEAR, forbiddenLinear)
					                 .build();

			assertThatIllegalArgumentException()
					.as("FORBIDDEN dim2: x2=99 must throw")
					.isThrownBy(() -> grid.interpolate(1.0, 99.0));
		}
	}

	@Nested
	@DisplayName("with invalid construction")
	final class WithInvalidConstruction
	{
		@Test
		@DisplayName("build() throws when data has fewer than two X1 slices")
		void buildThrowsOnSingleSlice()
		{
			List<GridSample<Double, Double, Double>> singleSlice = List.of(
					new GridSample<>(1.0, 0.0, 0.0), new GridSample<>(1.0, 1.0, 1.0)
			);

			assertThatIllegalArgumentException()
					.as("build() must throw when there is only one X1 slice")
					.isThrownBy(() ->
							InterpolationGrid.<Double, Double, Double>builder()
							                 .withData(singleSlice)
							                 .dimension1(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
							                 .dimension2(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
							                 .build());
		}

		@Test
		@DisplayName("build() throws when data is not provided")
		void buildThrowsWithoutData()
		{
			assertThatIllegalArgumentException()
					.as("build() must throw when data was not provided")
					.isThrownBy(() ->
							InterpolationGrid.<Double, Double, Double>builder()
							                 .dimension1(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
							                 .dimension2(DoubleInterpolatableSpace.LINEAR, LINEAR_FLAT)
							                 .build());
		}
	}
}
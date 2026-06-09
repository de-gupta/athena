package de.gupta.commons.utility.math.interpolation.grid;

import de.gupta.commons.utility.math.analysis.space.standard.DoubleInterpolatableSpace;
import de.gupta.commons.utility.math.analysis.space.standard.DoubleVectorSpaceStructure;
import de.gupta.commons.utility.math.analysis.space.standard.LocalDateInterpolatableSpace;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.data.GridSample;
import de.gupta.commons.utility.math.interpolation.method.LinearInterpolationMethod;
import de.gupta.commons.utility.math.interpolation.method.MonotoneCubicMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Vol surface interpolation — integration test")
final class VolSurfaceInterpolationTest
{
	private static final LocalDate MAR = LocalDate.of(2024, 3, 31);
	private static final LocalDate JUN = LocalDate.of(2024, 6, 30);
	private static final LocalDate SEP = LocalDate.of(2024, 9, 30);
	private static final LocalDate DEC = LocalDate.of(2024, 12, 31);

	private static final double[] STRIKES = {80.0, 90.0, 100.0, 110.0, 120.0};
	private static final double[][] VOLS = {
			{0.25, 0.22, 0.20, 0.21, 0.23},
			{0.26, 0.23, 0.21, 0.22, 0.24},
			{0.27, 0.24, 0.22, 0.23, 0.25},
			{0.28, 0.25, 0.23, 0.24, 0.26}
	};
	private static final LocalDate[] EXPIRIES = {MAR, JUN, SEP, DEC};

	private static InterpolationGrid<LocalDate, Double, Double> buildSurface()
	{
		List<GridSample<LocalDate, Double, Double>> samples = IntStream.range(0, 4)
		                                                               .boxed()
		                                                               .flatMap(expiryIndex -> IntStream.range(0, 5)
		                                                                                                .mapToObj(
				                                                                                                strikeIndex -> new GridSample<>(
																														EXPIRIES[expiryIndex],
																														STRIKES[strikeIndex],
																														VOLS[expiryIndex][strikeIndex])))
		                                                               .toList();

		return InterpolationGrid.<LocalDate, Double, Double>builder()
		                        .withData(samples)
		                        .dimension1(
										LocalDateInterpolatableSpace.INSTANCE,
										LinearInterpolationMethod.of(LocalDateInterpolatableSpace.INSTANCE,
												DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.FLAT))
		                        .dimension2(
										DoubleInterpolatableSpace.LOG,
										MonotoneCubicMethod.of(DoubleInterpolatableSpace.LOG,
												DoubleVectorSpaceStructure.INSTANCE, d -> d, f -> f,
												ExtrapolationPolicy.FLAT))
		                        .build();
	}

	@Nested
	@DisplayName("when queried at exact grid knots")
	final class WhenQueriedAtExactGridKnots
	{
		private final InterpolationGrid<LocalDate, Double, Double> surface = buildSurface();

		@Test
		@DisplayName("returns the exact ATM vol at each expiry")
		void returnsExactAtmVolAtEachExpiry()
		{
			assertThat(surface.interpolate(MAR, 100.0))
					.as("3-month ATM vol must be 0.20")
					.isCloseTo(0.20, within(1e-12));
			assertThat(surface.interpolate(JUN, 100.0))
					.as("6-month ATM vol must be 0.21")
					.isCloseTo(0.21, within(1e-12));
			assertThat(surface.interpolate(SEP, 100.0))
					.as("9-month ATM vol must be 0.22")
					.isCloseTo(0.22, within(1e-12));
			assertThat(surface.interpolate(DEC, 100.0))
					.as("12-month ATM vol must be 0.23")
					.isCloseTo(0.23, within(1e-12));
		}

		@Test
		@DisplayName("returns the exact wing vols at corner grid points")
		void returnsExactWingVols()
		{
			assertThat(surface.interpolate(MAR, 80.0))
					.as("3-month K=80 vol must be 0.25")
					.isCloseTo(0.25, within(1e-12));
			assertThat(surface.interpolate(DEC, 120.0))
					.as("12-month K=120 vol must be 0.26")
					.isCloseTo(0.26, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when interpolating in the expiry dimension")
	final class WhenInterpolatingInExpiryDimension
	{
		private final InterpolationGrid<LocalDate, Double, Double> surface = buildSurface();

		@Test
		@DisplayName("ATM vol at 2024-05-01 is linearly interpolated between 3-month and 6-month")
		void atmVolIsLinearlyInterpolatedBetweenNeighbouringExpiries()
		{
			double lambda = 31.0 / 91.0;
			double expected = 0.20 + lambda * 0.01;

			assertThat(surface.interpolate(LocalDate.of(2024, 5, 1), 100.0))
					.as("ATM vol on 2024-05-01 must be linearly interpolated from 3m and 6m slices")
					.isCloseTo(expected, within(1e-9));
		}

		@Test
		@DisplayName("interpolated vol is between adjacent expiry vols for any strike")
		void interpolatedVolIsBetweenAdjacentExpiryVols()
		{
			double mid = surface.interpolate(LocalDate.of(2024, 5, 15), 90.0);

			assertThat(mid)
					.as("vol at mid-expiry must be between 3m K=90 (0.22) and 6m K=90 (0.23)")
					.isBetween(0.22 - 1e-12, 0.23 + 1e-12);
		}
	}

	@Nested
	@DisplayName("when interpolating in the strike dimension with log-space monotone cubic")
	final class WhenInterpolatingInStrikeDimension
	{
		private final InterpolationGrid<LocalDate, Double, Double> surface = buildSurface();

		@Test
		@DisplayName("vol at K=95 is between ATM (0.21) and K=90 (0.23) vols at 6-month expiry")
		void volAtNearAtmStrikeIsBetweenAdjacentKnots()
		{
			double result = surface.interpolate(JUN, 95.0);

			assertThat(result)
					.as("6m K=95 vol must sit between K=90 (0.23) and K=100 (0.21)")
					.isBetween(0.21 - 1e-12, 0.23 + 1e-12);
		}

		@Test
		@DisplayName("interpolated smile does not dip below ATM vol in the smile region")
		void interpolatedSmileDoesNotDipBelowAtmVol()
		{
			for (double strike = 80.0; strike <= 120.0; strike += 2.5)
			{
				double vol = surface.interpolate(JUN, strike);
				assertThat(vol)
						.as("6m vol at K=" + strike + " must be >= ATM vol 0.21 (no undershoot)")
						.isGreaterThanOrEqualTo(0.21 - 1e-9);
			}
		}
	}

	@Nested
	@DisplayName("when queried at an arbitrary interior point")
	final class WhenQueriedAtArbitraryInteriorPoint
	{
		private final InterpolationGrid<LocalDate, Double, Double> surface = buildSurface();

		@Test
		@DisplayName("interior (expiry, strike) query returns a value in the range of the surface")
		void interiorQueryReturnsValueInSurfaceRange()
		{
			double result = surface.interpolate(LocalDate.of(2024, 8, 15), 105.0);

			assertThat(result)
					.as("interior query must return a vol between the surface minimum (0.20) and maximum (0.28)")
					.isBetween(0.20 - 1e-9, 0.28 + 1e-9);
		}
	}
}
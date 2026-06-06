package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

//question: what are we really testing here? just usual arithmetic? this class seems useless
@DisplayName("MetricSpace contract")
final class MetricSpaceContractTest
{
	private static final MetricSpace<Double> ABSOLUTE_DIFFERENCE = (a, b) -> Math.abs(a - b);
	private static final double CALCULATION_TOLERANCE_THRESHOLD = 1e-12;

	@Nested
	@DisplayName("distance(a, a)")
	final class SelfDistance
	{
		@Test
		@DisplayName("returns zero")
		void returnsZero()
		{
			assertThat(ABSOLUTE_DIFFERENCE.distance(3.0, 3.0)).isZero();
		}

		@Test
		@DisplayName("returns zero for negative values")
		void returnsZeroForNegativeValues()
		{
			assertThat(ABSOLUTE_DIFFERENCE.distance(-7.5, -7.5)).isZero();
		}
	}

	@Nested
	@DisplayName("distance(a, b)")
	final class PairwiseDistance
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("symmetricPairs")
		@DisplayName("is symmetric")
		void isSymmetric(final double a, final double b, final String description)
		{
			assertThat(ABSOLUTE_DIFFERENCE.distance(a, b))
					.as(description)
					.isCloseTo(ABSOLUTE_DIFFERENCE.distance(b, a), within(CALCULATION_TOLERANCE_THRESHOLD));
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("pairsWithKnownDistance")
		@DisplayName("returns the expected distance")
		void returnsExpectedDistance(final double a, final double b, final String description, final double expected)
		{
			assertThat(ABSOLUTE_DIFFERENCE.distance(a, b))
					.as(description)
					.isCloseTo(expected, within(CALCULATION_TOLERANCE_THRESHOLD));
		}

		@Test
		@DisplayName("satisfies triangle inequality")
		void satisfiesTriangleInequality()
		{
			double distanceAB = ABSOLUTE_DIFFERENCE.distance(1.0, 4.0);
			double distanceBC = ABSOLUTE_DIFFERENCE.distance(4.0, 7.0);
			double distanceAC = ABSOLUTE_DIFFERENCE.distance(1.0, 7.0);

			assertThat(distanceAC).isLessThanOrEqualTo(distanceAB + distanceBC + CALCULATION_TOLERANCE_THRESHOLD);
		}

		@Test
		@DisplayName("is non-negative for arbitrary inputs")
		void isNonNegative()
		{
			assertThat(ABSOLUTE_DIFFERENCE.distance(-100.0, 50.0)).isGreaterThanOrEqualTo(0.0);
		}

		private static Stream<Arguments> symmetricPairs()
		{
			return Stream.of(
					Arguments.of(1.0, 5.0, "distance(1, 5) == distance(5, 1)"),
					Arguments.of(-3.0, 2.0, "distance(-3, 2) == distance(2, -3)"),
					Arguments.of(0.0, 100.0, "distance(0, 100) == distance(100, 0)")
			);
		}

		private static Stream<Arguments> pairsWithKnownDistance()
		{
			return Stream.of(
					Arguments.of(0.0, 5.0, "distance from 0 to 5", 5.0),
					Arguments.of(3.0, 7.0, "distance from 3 to 7", 4.0),
					Arguments.of(-2.0, 3.0, "distance from -2 to 3", 5.0),
					Arguments.of(-5.0, -1.0, "distance between negative values", 4.0)
			);
		}
	}
}
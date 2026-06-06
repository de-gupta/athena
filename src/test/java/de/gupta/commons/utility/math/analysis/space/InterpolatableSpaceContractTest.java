package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

//question: what are we really testing here? just usual arithmetic? this class seems useless
@DisplayName("InterpolatableSpace contract")
final class InterpolatableSpaceContractTest
{
	private static final InterpolatableSpace<Double> LINEAR_DOUBLE_SPACE = new InterpolatableSpace<>()
	{
		@Override
		public double distance(final Double a, final Double b)
		{
			return Math.abs(a - b);
		}

		@Override
		public double parameter(final Double left, final Double right, final Double query)
		{
			return (query - left) / (right - left);
		}

		@Override
		public int compare(final Double a, final Double b)
		{
			return Double.compare(a, b);
		}
	};

	@Nested
	@DisplayName("parameter(left, right, query)")
	final class Parameter
	{
		@Test
		@DisplayName("returns zero when query equals left")
		void returnsZeroWhenQueryEqualsLeft()
		{
			assertThat(LINEAR_DOUBLE_SPACE.parameter(2.0, 6.0, 2.0)).isCloseTo(0.0, within(1e-12));
		}

		@Test
		@DisplayName("returns one when query equals right")
		void returnsOneWhenQueryEqualsRight()
		{
			assertThat(LINEAR_DOUBLE_SPACE.parameter(2.0, 6.0, 6.0)).isCloseTo(1.0, within(1e-12));
		}

		@Test
		@DisplayName("returns one half when query is the midpoint")
		void returnsOneHalfAtMidpoint()
		{
			assertThat(LINEAR_DOUBLE_SPACE.parameter(0.0, 10.0, 5.0)).isCloseTo(0.5, within(1e-12));
		}

		@ParameterizedTest(name = "{3}")
		@MethodSource("parametersAtKnownPositions")
		@DisplayName("returns the expected parameter value")
		void returnsExpectedParameter(final double left, final double right, final double query,
		                              final String description, final double expected)
		{
			assertThat(LINEAR_DOUBLE_SPACE.parameter(left, right, query))
					.as(description)
					.isCloseTo(expected, within(1e-12));
		}

		@ParameterizedTest(name = "{3}")
		@MethodSource("queriesBetweenBounds")
		@DisplayName("returns a value in [0, 1] when query is between left and right")
		void returnsValueInUnitIntervalForInteriorQuery(final double left, final double right, final double query,
		                                                final String description)
		{
			double result = LINEAR_DOUBLE_SPACE.parameter(left, right, query);

			assertThat(result).as(description).isBetween(0.0, 1.0);
		}

		private static Stream<Arguments> parametersAtKnownPositions()
		{
			return Stream.of(
					Arguments.of(0.0, 4.0, 1.0, "one quarter of the way", 0.25),
					Arguments.of(0.0, 4.0, 3.0, "three quarters of the way", 0.75),
					Arguments.of(10.0, 20.0, 15.0, "midpoint of [10, 20]", 0.5),
					Arguments.of(-2.0, 2.0, 0.0, "midpoint of [-2, 2]", 0.5)
			);
		}

		private static Stream<Arguments> queriesBetweenBounds()
		{
			return Stream.of(
					Arguments.of(0.0, 10.0, 3.7, "interior point in [0, 10]"),
					Arguments.of(-5.0, 5.0, -1.0, "interior point in [-5, 5]"),
					Arguments.of(100.0, 200.0, 150.0, "interior point in [100, 200]")
			);
		}
	}

	@Nested
	@DisplayName("compare(a, b)")
	final class Compare
	{
		@Test
		@DisplayName("returns negative when a is less than b")
		void returnsNegativeWhenLess()
		{
			assertThat(LINEAR_DOUBLE_SPACE.compare(1.0, 5.0)).isNegative();
		}

		@Test
		@DisplayName("returns positive when a is greater than b")
		void returnsPositiveWhenGreater()
		{
			assertThat(LINEAR_DOUBLE_SPACE.compare(5.0, 1.0)).isPositive();
		}

		@Test
		@DisplayName("returns zero when a equals b")
		void returnsZeroWhenEqual()
		{
			assertThat(LINEAR_DOUBLE_SPACE.compare(3.0, 3.0)).isZero();
		}

		@Test
		@DisplayName("is antisymmetric: sign of compare(a, b) is opposite to sign of compare(b, a)")
		void isAntisymmetric()
		{
			int forwardSign = Integer.signum(LINEAR_DOUBLE_SPACE.compare(2.0, 8.0));
			int reverseSign = Integer.signum(LINEAR_DOUBLE_SPACE.compare(8.0, 2.0));

			assertThat(forwardSign).isEqualTo(-reverseSign);
		}
	}

	@Nested
	@DisplayName("as Comparator")
	final class AsComparator
	{
		@Test
		@DisplayName("the space itself can be used as a Comparator")
		void canBeUsedAsComparator()
		{
			Comparator<Double> comparator = LINEAR_DOUBLE_SPACE;

			assertThat(comparator.compare(1.0, 2.0)).isNegative();
			assertThat(comparator.compare(2.0, 1.0)).isPositive();
			assertThat(comparator.compare(1.0, 1.0)).isZero(); // why called on itself?
		}
	}
}
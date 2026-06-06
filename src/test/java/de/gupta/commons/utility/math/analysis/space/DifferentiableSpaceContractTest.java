package de.gupta.commons.utility.math.analysis.space;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

//question: what are we really testing here? just usual arithmetic? this class seems useless
@DisplayName("DifferentiableSpace contract")
final class DifferentiableSpaceContractTest
{
	private static final DifferentiableSpace<Double> LINEAR_DOUBLE_SPACE = new DifferentiableSpace<>()
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
	@DisplayName("span(left, right)")
	final class Span
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("pairsWithKnownSpan")
		@DisplayName("returns the expected interval length")
		void returnsExpectedIntervalLength(final double left, final double right, final String description,
		                                   final double expected)
		{
			assertThat(LINEAR_DOUBLE_SPACE.span(left, right))
					.as(description)
					.isCloseTo(expected, within(1e-12));
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("symmetricPairs")
		@DisplayName("is symmetric")
		void isSymmetric(final double a, final double b, final String description)
		{
			assertThat(LINEAR_DOUBLE_SPACE.span(a, b))
					.as(description)
					.isCloseTo(LINEAR_DOUBLE_SPACE.span(b, a), within(1e-12));
		}

		@Test
		@DisplayName("is non-negative")
		void isNonNegative()
		{
			assertThat(LINEAR_DOUBLE_SPACE.span(5.0, 1.0)).isGreaterThanOrEqualTo(0.0);
		}

		@Test
		@DisplayName("equals distance by default")
		void equalsDistanceByDefault()
		{
			double spanValue = LINEAR_DOUBLE_SPACE.span(2.0, 7.0);
			double distanceValue = LINEAR_DOUBLE_SPACE.distance(2.0, 7.0);

			assertThat(spanValue).isCloseTo(distanceValue, within(1e-12));
		}

		@Test
		@DisplayName("throws NullPointerException when left is null")
		void throwsWhenLeftIsNull()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> LINEAR_DOUBLE_SPACE.span(null, 5.0))
					.withMessage("left");
		}

		@Test
		@DisplayName("throws NullPointerException when right is null")
		void throwsWhenRightIsNull()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> LINEAR_DOUBLE_SPACE.span(5.0, null))
					.withMessage("right");
		}

		private static Stream<Arguments> pairsWithKnownSpan()
		{
			return Stream.of(
					Arguments.of(0.0, 5.0, "span of [0, 5]", 5.0),
					Arguments.of(3.0, 10.0, "span of [3, 10]", 7.0),
					Arguments.of(-4.0, 4.0, "span of [-4, 4]", 8.0),
					Arguments.of(1.0, 1.0, "span of a degenerate interval", 0.0)
			);
		}

		private static Stream<Arguments> symmetricPairs()
		{
			return Stream.of(
					Arguments.of(1.0, 5.0, "span(1, 5) == span(5, 1)"),
					Arguments.of(-3.0, 3.0, "span(-3, 3) == span(3, -3)"),
					Arguments.of(0.0, 100.0, "span(0, 100) == span(100, 0)")
			);
		}
	}
}
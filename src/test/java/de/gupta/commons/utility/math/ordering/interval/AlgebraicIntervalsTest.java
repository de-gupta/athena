package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlgebraicIntervals")
final class AlgebraicIntervalsTest
{
	private static BoundedInterval<IntegralNumber> closed(final long a, final long b)
	{
		return Intervals.closed(n(a), n(b));
	}

	private static IntegralNumber n(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	@Nested
	@DisplayName("when computing length")
	final class WhenComputingLength
	{
		@Test
		@DisplayName("returns upper minus lower for closed interval")
		void returnsUpperMinusLowerForClosedInterval()
		{
			assertThat(AlgebraicIntervals.length(closed(2, 7))).as("[2,7]").isEqualTo(n(5));
			assertThat(AlgebraicIntervals.length(closed(0, 10))).as("[0,10]").isEqualTo(n(10));
			assertThat(AlgebraicIntervals.length(closed(3, 3))).as("[3,3] point").isEqualTo(n(0));
		}

		@Test
		@DisplayName("returns upper minus lower regardless of open or closed bounds")
		void returnsUpperMinusLowerRegardlessOfBoundType()
		{
			assertThat(AlgebraicIntervals.length(Intervals.open(n(1), n(5)))).as("(1,5)").isEqualTo(n(4));
			assertThat(AlgebraicIntervals.length(Intervals.closedOpen(n(1), n(5)))).as("[1,5)").isEqualTo(n(4));
		}
	}

	@Nested
	@DisplayName("when shifting")
	final class WhenShifting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("shiftIntervalDispatchCases")
		@DisplayName("shift(interval, delta) dispatches to bounded and unbounded implementations")
		void shiftIntervalDispatchesToBoundedAndUnboundedImplementations(
				final String as, final Interval<IntegralNumber> interval,
				final IntegralNumber delta, final Interval<IntegralNumber> expected)
		{
			assertThat(AlgebraicIntervals.shift(interval, delta)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("shifts bounded interval by delta preserving bound types")
		void shiftsBoundedIntervalByDeltaPreservingBoundTypes()
		{
			assertThat(AlgebraicIntervals.shift(closed(1, 5), n(3))).as("[1,5] + 3").isEqualTo(closed(4, 8));
			assertThat(AlgebraicIntervals.shift(closed(1, 5), n(-2))).as("[1,5] - 2").isEqualTo(closed(-1, 3));
			assertThat(AlgebraicIntervals.shift(Intervals.open(n(1), n(5)), n(2))).as("(1,5) + 2")
			                                                                      .isEqualTo(
																						  Intervals.open(n(3), n(7)));
		}

		@Test
		@DisplayName("shifts unbounded interval shifting finite bound only")
		void shiftsUnboundedIntervalShiftingFiniteBoundOnly()
		{
			UnboundedInterval<IntegralNumber> result = AlgebraicIntervals.shift(
					Intervals.atLeast(n(1)), n(3));
			assertThat(result.lowerBound()).isPresent();
			assertThat(result.lowerBound().get().value()).isEqualTo(n(4));
			assertThat(result.upperBound()).isEmpty();

			UnboundedInterval<IntegralNumber> result2 = AlgebraicIntervals.shift(
					Intervals.atMost(n(10)), n(-5));
			assertThat(result2.upperBound()).isPresent();
			assertThat(result2.upperBound().get().value()).isEqualTo(n(5));
			assertThat(result2.lowerBound()).isEmpty();
		}

		private static Stream<Arguments> shiftIntervalDispatchCases()
		{
			return Stream.of(
					Arguments.of("bounded interval shifts through interval overload", closed(1, 5), n(2), closed(3, 7)),
					Arguments.of("lower-bounded interval shifts through interval overload", Intervals.atLeast(n(1)),
							n(2),
							Intervals.atLeast(n(3))),
					Arguments.of("upper-bounded interval shifts through interval overload", Intervals.atMost(n(5)),
							n(-2),
							Intervals.atMost(n(3))),
					Arguments.of("fully unbounded interval remains fully unbounded", Intervals.all(), n(4),
							Intervals.all())
			);
		}
	}

	@Nested
	@DisplayName("when computing midpoint")
	final class WhenComputingMidpoint
	{
		@Test
		@DisplayName("returns exact midpoint when sum is even")
		void returnsExactMidpointWhenSumIsEven()
		{
			assertThat(AlgebraicIntervals.midpoint(closed(2, 8), RoundingStrategies.floor()))
					.as("[2,8] midpoint").isEqualTo(n(5));
			assertThat(AlgebraicIntervals.midpoint(closed(0, 10), RoundingStrategies.floor()))
					.as("[0,10] midpoint").isEqualTo(n(5));
		}

		@Test
		@DisplayName("applies rounding strategy when sum is odd")
		void appliesRoundingStrategyWhenSumIsOdd()
		{
			assertThat(AlgebraicIntervals.midpoint(closed(1, 6), RoundingStrategies.floor()))
					.as("[1,6] floor midpoint").isEqualTo(n(3));
			assertThat(AlgebraicIntervals.midpoint(closed(1, 6), RoundingStrategies.ceiling()))
					.as("[1,6] ceiling midpoint").isEqualTo(n(4));
		}
	}
}
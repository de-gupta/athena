package de.gupta.commons.utility.math.ordering.element.interval;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlgebraicIntervals")
final class AlgebraicIntervalsTest
{
	private static BoundedInterval<IntegersAsEuclideanDomain> closed(final long a, final long b)
	{
		return Intervals.closed(n(a), n(b));
	}

	private static IntegersAsEuclideanDomain n(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
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
			UnboundedInterval<IntegersAsEuclideanDomain> result = AlgebraicIntervals.shift(
					Intervals.atLeast(n(1)), n(3));
			assertThat(result.lowerBound()).isPresent();
			assertThat(result.lowerBound().get().value()).isEqualTo(n(4));
			assertThat(result.upperBound()).isEmpty();

			UnboundedInterval<IntegersAsEuclideanDomain> result2 = AlgebraicIntervals.shift(
					Intervals.atMost(n(10)), n(-5));
			assertThat(result2.upperBound()).isPresent();
			assertThat(result2.upperBound().get().value()).isEqualTo(n(5));
			assertThat(result2.lowerBound()).isEmpty();
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
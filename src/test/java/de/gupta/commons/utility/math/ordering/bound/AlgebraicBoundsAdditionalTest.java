package de.gupta.commons.utility.math.ordering.bound;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.interval.Interval;
import de.gupta.commons.utility.math.ordering.interval.Intervals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlgebraicBounds additional interval coverage")
final class AlgebraicBoundsAdditionalTest
{
	private static Interval<IntElement> closedInterval(final int lower, final int upper)
	{
		return Intervals.closed(element(lower), element(upper));
	}

	private static IntElement element(final int value)
	{
		return new IntElement(value);
	}

	private record IntElement(int value) implements TotallyOrdered<IntElement>
	{
		@Override
		public OrderRelation compare(final IntElement other)
		{
			return OrderRelation.from(Integer.compare(value, other.value));
		}
	}

	@Nested
	@DisplayName("when subsuming from below across interval shapes")
	final class WhenSubsumingFromBelowAcrossIntervalShapes
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheExpectedResultCases")
		@DisplayName("returns the expected result")
		void returnsTheExpectedResult(final String as, final Interval<IntElement> first,
		                              final Interval<IntElement> second, final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromBelow(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsTheExpectedResultCases()
		{
			return Stream.of(
					Arguments.of(
							"bounded interval subsumes lower-bounded unbounded interval when its lower bound is lower",
							closedInterval(1, 5), Intervals.atLeast(element(3)), true),
					Arguments.of(
							"bounded interval does not subsume lower-bounded unbounded interval when its lower bound is higher",
							closedInterval(3, 5), Intervals.atLeast(element(1)), false),
					Arguments.of("lower-bounded interval does not subsume all from below",
							Intervals.atLeast(element(3)),
							Intervals.all(), false)
			);
		}
	}
}
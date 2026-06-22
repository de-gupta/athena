package de.gupta.commons.utility.math.ordering.bound;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.element.interval.Interval;
import de.gupta.commons.utility.math.ordering.element.interval.Intervals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlgebraicBounds")
final class AlgebraicBoundsTest
{
	private static Bound<IntElement> closedBound(final int value)
	{
		return new Bound.Closed<>(element(value));
	}

	private static IntElement element(final int value)
	{
		return new IntElement(value);
	}

	private static Bound<IntElement> openBound(final int value)
	{
		return new Bound.Open<>(element(value));
	}

	private static de.gupta.commons.utility.math.ordering.element.interval.BoundedInterval<IntElement> closedInterval(
			final int lower, final int upper)
	{
		return Intervals.closed(element(lower), element(upper));
	}

	private record IntElement(int value) implements TotallyOrdered<IntElement>
	{
		@Override
		public OrderRelation compare(final IntElement other)
		{
			int result = Integer.compare(value, other.value);
			return result < 0 ? OrderRelation.LESS_THAN : result > 0 ? OrderRelation.GREATER_THAN : OrderRelation.EQUAL;
		}
	}

	@Nested
	@DisplayName("when subsuming from below (bound vs bound)")
	final class WhenSubsumingFromBelowBoundVsBound
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("subsumesFromBelowBoundVsBoundCases")
		@DisplayName("returns correct result based on open/closed semantics")
		void returnsCorrectResultBasedOnOpenClosedSemantics(final String as,
		                                                    final Bound<IntElement> first,
		                                                    final Bound<IntElement> second,
		                                                    final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromBelow(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> subsumesFromBelowBoundVsBoundCases()
		{
			return Stream.of(
					Arguments.of("closed(1) subsumesFromBelow closed(3)", closedBound(1), closedBound(3), true),
					Arguments.of("closed(3) does not subsumeFromBelow closed(1)", closedBound(3), closedBound(1),
							false),
					Arguments.of("closed(3) subsumesFromBelow closed(3)", closedBound(3), closedBound(3), true),

					Arguments.of("open(1) subsumesFromBelow closed(3)", openBound(1), closedBound(3), true),
					Arguments.of("open(3) does not subsumeFromBelow closed(3)", openBound(3), closedBound(3), false),
					Arguments.of("open(3) does not subsumeFromBelow closed(1)", openBound(3), closedBound(1), false),

					Arguments.of("open(3) subsumesFromBelow open(3)", openBound(3), openBound(3), true),
					Arguments.of("open(1) subsumesFromBelow open(3)", openBound(1), openBound(3), true),
					Arguments.of("open(3) does not subsumeFromBelow open(1)", openBound(3), openBound(1), false),

					Arguments.of("closed(3) subsumesFromBelow open(3)", closedBound(3), openBound(3), true),
					Arguments.of("open(3) does not subsumeFromBelow closed(3)", openBound(3), closedBound(3), false)
			);
		}
	}

	@Nested
	@DisplayName("when subsuming from above (bound vs bound)")
	final class WhenSubsumingFromAboveBoundVsBound
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("subsumesFromAboveBoundVsBoundCases")
		@DisplayName("returns correct result based on open/closed semantics")
		void returnsCorrectResultBasedOnOpenClosedSemantics(final String as,
		                                                    final Bound<IntElement> first,
		                                                    final Bound<IntElement> second,
		                                                    final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromAbove(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> subsumesFromAboveBoundVsBoundCases()
		{
			return Stream.of(
					Arguments.of("closed(3) subsumesFromAbove closed(1)", closedBound(3), closedBound(1), true),
					Arguments.of("closed(1) does not subsumeFromAbove closed(3)", closedBound(1), closedBound(3),
							false),
					Arguments.of("closed(3) subsumesFromAbove closed(3)", closedBound(3), closedBound(3), true),

					Arguments.of("open(3) subsumesFromAbove closed(1)", openBound(3), closedBound(1), true),
					Arguments.of("open(3) does not subsumeFromAbove closed(3)", openBound(3), closedBound(3), false),
					Arguments.of("open(1) does not subsumeFromAbove closed(3)", openBound(1), closedBound(3), false),

					Arguments.of("open(3) subsumesFromAbove open(3)", openBound(3), openBound(3), true),
					Arguments.of("open(3) subsumesFromAbove open(1)", openBound(3), openBound(1), true),
					Arguments.of("open(1) does not subsumeFromAbove open(3)", openBound(1), openBound(3), false),

					Arguments.of("closed(3) subsumesFromAbove open(3)", closedBound(3), openBound(3), true),
					Arguments.of("open(3) does not subsumeFromAbove closed(3)", openBound(3), closedBound(3), false)
			);
		}
	}

	@Nested
	@DisplayName("when subsuming optional bounds")
	final class WhenSubsumingOptionalBounds
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("subsumesFromBelowBoundVsOptionalCases")
		@DisplayName("subsumesFromBelow(bound, Optional) treats empty Optional as subsumed")
		void subsumesFromBelowTreatsEmptyOptionalAsSubsumed(final String as,
		                                                    final Bound<IntElement> first,
		                                                    final Optional<Bound<IntElement>> second,
		                                                    final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromBelow(first, second)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("subsumesFromAboveBoundVsOptionalCases")
		@DisplayName("subsumesFromAbove(bound, Optional) treats empty Optional as subsumed")
		void subsumesFromAboveTreatsEmptyOptionalAsSubsumed(final String as,
		                                                    final Bound<IntElement> first,
		                                                    final Optional<Bound<IntElement>> second,
		                                                    final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromAbove(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> subsumesFromBelowBoundVsOptionalCases()
		{
			return Stream.of(
					Arguments.of("closed(3) does not subsumeFromBelow Optional.empty()", closedBound(3),
							Optional.empty(),
							false),
					Arguments.of("open(3) does not subsumeFromBelow Optional.empty()", openBound(3), Optional.empty(),
							false),
					Arguments.of("closed(1) subsumesFromBelow Optional[closed(3)]", closedBound(1),
							Optional.of(closedBound(3)), true),
					Arguments.of("open(3) does not subsumeFromBelow Optional[closed(3)]", openBound(3),
							Optional.of(closedBound(3)), false)
			);
		}

		private static Stream<Arguments> subsumesFromAboveBoundVsOptionalCases()
		{
			return Stream.of(
					Arguments.of("closed(3) does not subsumeFromAbove Optional.empty()", closedBound(3),
							Optional.empty(),
							false),
					Arguments.of("open(3) does not subsumeFromAbove Optional.empty()", openBound(3), Optional.empty(),
							false),
					Arguments.of("closed(3) subsumesFromAbove Optional[closed(1)]", closedBound(3),
							Optional.of(closedBound(1)), true),
					Arguments.of("open(3) does not subsumeFromAbove Optional[closed(3)]", openBound(3),
							Optional.of(closedBound(3)), false)
			);
		}
	}

	@Nested
	@DisplayName("when subsuming from below (interval vs interval)")
	final class WhenSubsumingFromBelowIntervalVsInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("subsumesFromBelowIntervalVsIntervalCases")
		@DisplayName("returns correct result based on lower-bound semantics")
		void returnsCorrectResultBasedOnLowerBoundSemantics(final String as,
		                                                    final Interval<IntElement> first,
		                                                    final Interval<IntElement> second,
		                                                    final boolean expected)
		{
			assertThat(AlgebraicBounds.subsumesFromBelow(first, second)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> subsumesFromBelowIntervalVsIntervalCases()
		{
			return Stream.of(
					Arguments.of("[1,5] subsumesFromBelow [3,7]", closedInterval(1, 5), closedInterval(3, 7), true),
					Arguments.of("[3,7] does not subsumeFromBelow [1,5]", closedInterval(3, 7), closedInterval(1, 5),
							false),
					Arguments.of("[3,∞) subsumesFromBelow [7,9]", Intervals.atLeast(element(3)), closedInterval(7, 9),
							true),
					Arguments.of("[7,∞) does not subsumeFromBelow [3,9]", Intervals.atLeast(element(7)),
							closedInterval(3, 9), false),
					Arguments.of("[3,∞) subsumesFromBelow [7,∞)", Intervals.atLeast(element(3)),
							Intervals.atLeast(element(7)), true),
					Arguments.of("[7,∞) does not subsumeFromBelow [3,∞)", Intervals.atLeast(element(7)),
							Intervals.atLeast(element(3)), false),

					Arguments.of("(-∞,∞) subsumesFromBelow [1,5]", Intervals.all(), closedInterval(1, 5), true),
					Arguments.of("[1,5] does not subsumeFromBelow (-∞,∞)", closedInterval(1, 5), Intervals.all(), false)
			);
		}
	}
}
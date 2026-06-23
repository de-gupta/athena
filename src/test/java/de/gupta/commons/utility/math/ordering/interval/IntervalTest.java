package de.gupta.commons.utility.math.ordering.interval;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.element.TotallyOrdered;
import de.gupta.commons.utility.math.ordering.structure.IntegerNaturalOrder;
import de.gupta.commons.utility.math.ordering.structure.TotalOrderStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Interval")
final class IntervalTest
{
	private static BoundedInterval<IntElement> closed(final int a, final int b)
	{
		return Intervals.closed(e(a), e(b));
	}

	private static IntElement e(final int value)
	{
		return new IntElement(value);
	}

	private static BoundedInterval<IntElement> open(final int a, final int b)
	{
		return Intervals.open(e(a), e(b));
	}

	private static BoundedInterval<IntElement> closedOpen(final int a, final int b)
	{
		return Intervals.closedOpen(e(a), e(b));
	}

	private static BoundedInterval<IntElement> openClosed(final int a, final int b)
	{
		return Intervals.openClosed(e(a), e(b));
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
	@DisplayName("when constructing bounded")
	final class WhenConstructingBounded
	{
		@Test
		@DisplayName("throws when lower exceeds upper")
		void throwsWhenLowerExceedsUpper()
		{
			assertThatThrownBy(() -> Intervals.closed(e(5), e(3))).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> Intervals.open(e(5), e(3))).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("throws for open or half-open interval with equal bounds")
		void throwsForOpenWithEqualBounds()
		{
			assertThatThrownBy(() -> Intervals.open(e(3), e(3))).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> Intervals.closedOpen(e(3), e(3))).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> Intervals.openClosed(e(3), e(3))).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("closed interval with equal bounds is a point")
		void closedIntervalWithEqualBoundsIsAPoint()
		{
			assertThat(closed(3, 3).isPoint()).isEqualTo(true);
		}
	}

	@Nested
	@DisplayName("when constructing unbounded")
	final class WhenConstructingUnbounded
	{
		@Test
		@DisplayName("atLeast produces lower-bounded interval containing all elements at or above")
		void atLeastProducesLowerBoundedInterval()
		{
			UnboundedInterval<IntElement> interval = Intervals.atLeast(e(5));
			assertThat(interval.contains(e(5))).isEqualTo(true);
			assertThat(interval.contains(e(10))).isEqualTo(true);
			assertThat(interval.contains(e(4))).isEqualTo(false);
		}

		@Test
		@DisplayName("greaterThan produces strictly lower-bounded interval excluding the bound")
		void greaterThanProducesStrictlyLowerBoundedInterval()
		{
			UnboundedInterval<IntElement> interval = Intervals.greaterThan(e(5));
			assertThat(interval.contains(e(5))).isEqualTo(false);
			assertThat(interval.contains(e(6))).isEqualTo(true);
		}

		@Test
		@DisplayName("atMost produces upper-bounded interval containing all elements at or below")
		void atMostProducesUpperBoundedInterval()
		{
			UnboundedInterval<IntElement> interval = Intervals.atMost(e(5));
			assertThat(interval.contains(e(5))).isEqualTo(true);
			assertThat(interval.contains(e(0))).isEqualTo(true);
			assertThat(interval.contains(e(6))).isEqualTo(false);
		}

		@Test
		@DisplayName("lessThan produces strictly upper-bounded interval excluding the bound")
		void lessThanProducesStrictlyUpperBoundedInterval()
		{
			UnboundedInterval<IntElement> interval = Intervals.lessThan(e(5));
			assertThat(interval.contains(e(5))).isEqualTo(false);
			assertThat(interval.contains(e(4))).isEqualTo(true);
		}

		@Test
		@DisplayName("all contains every element")
		void allContainsEveryElement()
		{
			UnboundedInterval<IntElement> interval = Intervals.all();
			assertThat(interval.contains(e(Integer.MIN_VALUE))).isEqualTo(true);
			assertThat(interval.contains(e(0))).isEqualTo(true);
			assertThat(interval.contains(e(Integer.MAX_VALUE))).isEqualTo(true);
		}
	}

	@Nested
	@DisplayName("when checking contains(element)")
	final class WhenCheckingContainsElement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsElementCases")
		@DisplayName("returns correct result based on bound types")
		void returnsCorrectResultBasedOnBoundTypes(final String as, final Interval<IntElement> interval,
		                                           final int element, final boolean expected)
		{
			assertThat(interval.contains(e(element))).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsElementCases()
		{
			return Stream.of(
					Arguments.of("[1,5] contains 1", closed(1, 5), 1, true),
					Arguments.of("[1,5] contains 3", closed(1, 5), 3, true),
					Arguments.of("[1,5] contains 5", closed(1, 5), 5, true),
					Arguments.of("[1,5] excludes 0", closed(1, 5), 0, false),
					Arguments.of("[1,5] excludes 6", closed(1, 5), 6, false),
					Arguments.of("(1,5) excludes 1", open(1, 5), 1, false),
					Arguments.of("(1,5) contains 2", open(1, 5), 2, true),
					Arguments.of("(1,5) excludes 5", open(1, 5), 5, false),
					Arguments.of("[1,5) excludes 5", closedOpen(1, 5), 5, false),
					Arguments.of("[1,5) contains 4", closedOpen(1, 5), 4, true),
					Arguments.of("(1,5] excludes 1", openClosed(1, 5), 1, false),
					Arguments.of("(1,5] contains 5", openClosed(1, 5), 5, true),
					Arguments.of("[3,∞) contains 10", Intervals.atLeast(e(3)), 10, true),
					Arguments.of("[3,∞) excludes 2", Intervals.atLeast(e(3)), 2, false),
					Arguments.of("(-∞,5) contains 4", Intervals.lessThan(e(5)), 4, true),
					Arguments.of("(-∞,5) excludes 5", Intervals.lessThan(e(5)), 5, false),
					Arguments.of("(-∞,∞) contains 0", Intervals.all(), 0, true)
			);
		}
	}

	@Nested
	@DisplayName("when checking isPoint")
	final class WhenCheckingIsPoint
	{
		@Test
		@DisplayName("closed interval with equal bounds is a point")
		void closedIntervalWithEqualBoundsIsAPoint()
		{
			assertThat(closed(3, 3).isPoint()).isEqualTo(true);
			assertThat(Intervals.point(e(7)).isPoint()).isEqualTo(true);
		}

		@Test
		@DisplayName("interval with different bounds is not a point")
		void intervalWithDifferentBoundsIsNotAPoint()
		{
			assertThat(closed(1, 5).isPoint()).isEqualTo(false);
		}
	}

	@Nested
	@DisplayName("when checking contains(interval)")
	final class WhenCheckingContainsInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsIntervalCases")
		@DisplayName("returns correct containment result")
		void returnsCorrectContainmentResult(final String as, final BoundedInterval<IntElement> outer,
		                                     final Interval<IntElement> inner, final boolean expected)
		{
			assertThat(outer.contains(inner)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsIntervalCases()
		{
			return Stream.of(
					Arguments.of("[1,7] contains [3,5]", closed(1, 7), closed(3, 5), true),
					Arguments.of("[1,7] contains itself", closed(1, 7), closed(1, 7), true),
					Arguments.of("[1,7] contains (1,7)", closed(1, 7), open(1, 7), true),
					Arguments.of("(1,7) does not contain [1,7]", open(1, 7), closed(1, 7), false),
					Arguments.of("[1,5] does not contain [3,7]", closed(1, 5), closed(3, 7), false),
					Arguments.of("[1,7] does not contain [1,∞)", closed(1, 7), Intervals.atLeast(e(1)), false),
					Arguments.of("[1,7] does not contain (-∞,7]", closed(1, 7), Intervals.atMost(e(7)), false),
					Arguments.of("[1,7] contains [5,7]", closed(1, 7), closed(5, 7), true)
			);
		}
	}

	@Nested
	@DisplayName("when checking contains(interval) on unbounded interval")
	final class WhenCheckingContainsIntervalOnUnboundedInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsIntervalOnUnboundedIntervalCases")
		@DisplayName("returns correct containment result")
		void returnsCorrectContainmentResult(final String as, final Interval<IntElement> outer,
		                                     final Interval<IntElement> inner, final boolean expected)
		{
			assertThat(outer).as("%s: outer type", as).isInstanceOf(UnboundedInterval.class);
			assertThat(outer.contains(inner)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsIntervalOnUnboundedIntervalCases()
		{
			return Stream.of(
					Arguments.of("(-∞,∞) contains [1,5]", Intervals.all(), closed(1, 5), true),
					Arguments.of("(-∞,∞) contains [3,∞)", Intervals.all(), Intervals.atLeast(e(3)), true),
					Arguments.of("(-∞,∞) contains (3,∞)", Intervals.all(), Intervals.greaterThan(e(3)), true),
					Arguments.of("(-∞,∞) contains (-∞,3]", Intervals.all(), Intervals.atMost(e(3)), true),
					Arguments.of("(-∞,∞) contains (-∞,3)", Intervals.all(), Intervals.lessThan(e(3)), true),

					Arguments.of("[3,∞) contains [5,7]", Intervals.atLeast(e(3)), closed(5, 7), true),
					Arguments.of("[3,∞) contains [3,7]", Intervals.atLeast(e(3)), closed(3, 7), true),
					Arguments.of("[3,∞) contains (3,∞)", Intervals.atLeast(e(3)), Intervals.greaterThan(e(3)), true),
					Arguments.of("[3,∞) contains [3,∞)", Intervals.atLeast(e(3)), Intervals.atLeast(e(3)), true),
					Arguments.of("[3,∞) does not contain [1,5]", Intervals.atLeast(e(3)), closed(1, 5), false),
					Arguments.of("[3,∞) contains [5,∞)", Intervals.atLeast(e(3)), Intervals.atLeast(e(5)), true),
					Arguments.of("[3,∞) does not contain (-∞,5]", Intervals.atLeast(e(3)), Intervals.atMost(e(5)),
							false),
					Arguments.of("[3,∞) does not contain (-∞,∞)", Intervals.atLeast(e(3)), Intervals.all(), false),

					Arguments.of("(3,∞) does not contain [3,7]", Intervals.greaterThan(e(3)), closed(3, 7), false),
					Arguments.of("(3,∞) contains [4,7]", Intervals.greaterThan(e(3)), closed(4, 7), true),
					Arguments.of("(3,∞) contains [4,∞)", Intervals.greaterThan(e(3)), Intervals.atLeast(e(4)), true),
					Arguments.of("(3,∞) contains (3,∞)", Intervals.greaterThan(e(3)), Intervals.greaterThan(e(3)),
							true),
					Arguments.of("(3,∞) does not contain [3,∞)", Intervals.greaterThan(e(3)), Intervals.atLeast(e(3)),
							false),

					Arguments.of("(-∞,5] contains [1,5]", Intervals.atMost(e(5)), closed(1, 5), true),
					Arguments.of("(-∞,5] does not contain [1,7]", Intervals.atMost(e(5)), closed(1, 7), false),
					Arguments.of("(-∞,5] contains (-∞,3]", Intervals.atMost(e(5)), Intervals.atMost(e(3)), true),
					Arguments.of("(-∞,5] contains (-∞,5)", Intervals.atMost(e(5)), Intervals.lessThan(e(5)), true),
					Arguments.of("(-∞,5) does not contain (-∞,5]", Intervals.lessThan(e(5)), Intervals.atMost(e(5)),
							false),
					Arguments.of("(-∞,5] does not contain (-∞,∞)", Intervals.atMost(e(5)), Intervals.all(), false)
			);
		}
	}

	@Nested
	@DisplayName("when intersecting bounded intervals")
	final class WhenIntersectingBoundedIntervals
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intersectCases")
		@DisplayName("produces the correct intersection")
		void producesTheCorrectIntersection(final String as, final BoundedInterval<IntElement> a,
		                                    final BoundedInterval<IntElement> b,
		                                    final Optional<BoundedInterval<IntElement>> expected)
		{
			assertThat(a.intersect(b)).as(as).isEqualTo(expected);
			assertThat(b.intersect(a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> intersectCases()
		{
			return Stream.of(
					Arguments.of("[1,5] ∩ [3,7] = [3,5]", closed(1, 5), closed(3, 7), Optional.of(closed(3, 5))),
					Arguments.of("[1,5) ∩ [3,7] = [3,5)", closedOpen(1, 5), closed(3, 7),
							Optional.of(closedOpen(3, 5))),
					Arguments.of("[1,5] ∩ [5,7] = [5,5]", closed(1, 5), closed(5, 7), Optional.of(closed(5, 5))),
					Arguments.of("[1,5] ∩ (5,7] = empty", closed(1, 5), openClosed(5, 7), Optional.empty()),
					Arguments.of("[1,3] ∩ [5,7] = empty", closed(1, 3), closed(5, 7), Optional.empty())
			);
		}
	}

	@Nested
	@DisplayName("when intersecting bounded with unbounded interval")
	final class WhenIntersectingBoundedWithUnbounded
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intersectCases")
		@DisplayName("produces the correct intersection (symmetric)")
		void producesTheCorrectIntersection(final String as, final BoundedInterval<IntElement> bounded,
		                                    final UnboundedInterval<IntElement> unbounded,
		                                    final Optional<Interval<IntElement>> expected)
		{
			assertThat(bounded.intersect(unbounded)).as(as).isEqualTo(expected);
			assertThat(unbounded.intersect(bounded)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> intersectCases()
		{
			return Stream.of(
					Arguments.of("[1,5] ∩ [3,∞) = [3,5]", closed(1, 5), Intervals.atLeast(e(3)),
							bounded(closed(3, 5))),
					Arguments.of("[1,5] ∩ (3,∞) = (3,5]", closed(1, 5), Intervals.greaterThan(e(3)),
							bounded(openClosed(3, 5))),
					Arguments.of("[1,5] ∩ [6,∞) = empty", closed(1, 5), Intervals.atLeast(e(6)),
							Optional.empty()),
					Arguments.of("[1,5] ∩ (-∞,3] = [1,3]", closed(1, 5), Intervals.atMost(e(3)),
							bounded(closed(1, 3))),
					Arguments.of("[1,5] ∩ (-∞,3) = [1,3)", closed(1, 5), Intervals.lessThan(e(3)),
							bounded(closedOpen(1, 3))),
					Arguments.of("[1,5] ∩ (-∞,0] = empty", closed(1, 5), Intervals.atMost(e(0)),
							Optional.empty()),
					Arguments.of("[1,5] ∩ (-∞,∞) = [1,5]", closed(1, 5), Intervals.all(),
							bounded(closed(1, 5)))
			);
		}

		private static Optional<Interval<IntElement>> bounded(final BoundedInterval<IntElement> b)
		{
			return Optional.of(b);
		}
	}

	@Nested
	@DisplayName("when intersecting unbounded intervals")
	final class WhenIntersectingUnboundedIntervals
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intersectCases")
		@DisplayName("produces the correct intersection")
		void producesTheCorrectIntersection(final String as, final UnboundedInterval<IntElement> a,
		                                    final UnboundedInterval<IntElement> b,
		                                    final Optional<Interval<IntElement>> expected)
		{
			assertThat(a.intersect(b)).as(as).isEqualTo(expected);
			assertThat(b.intersect(a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> intersectCases()
		{
			return Stream.of(
					Arguments.of("[3,∞) ∩ [5,∞) = [5,∞)", Intervals.atLeast(e(3)), Intervals.atLeast(e(5)),
							unbounded(Intervals.atLeast(e(5)))),
					Arguments.of("[3,∞) ∩ (5,∞) = (5,∞)", Intervals.atLeast(e(3)), Intervals.greaterThan(e(5)),
							unbounded(Intervals.greaterThan(e(5)))),
					Arguments.of("(-∞,5] ∩ (-∞,3] = (-∞,3]", Intervals.atMost(e(5)), Intervals.atMost(e(3)),
							unbounded(Intervals.atMost(e(3)))),
					Arguments.of("[3,∞) ∩ (-∞,7] = [3,7]", Intervals.atLeast(e(3)), Intervals.atMost(e(7)),
							bounded(closed(3, 7))),
					Arguments.of("[5,∞) ∩ (-∞,3] = empty", Intervals.atLeast(e(5)), Intervals.atMost(e(3)),
							Optional.empty()),
					Arguments.of("(-∞,∞) ∩ [3,∞) = [3,∞)", Intervals.all(), Intervals.atLeast(e(3)),
							unbounded(Intervals.atLeast(e(3)))),
					Arguments.of("(-∞,∞) ∩ (-∞,∞) = (-∞,∞)", Intervals.all(), Intervals.all(),
							unbounded(Intervals.all()))
			);
		}

		private static Optional<Interval<IntElement>> unbounded(final UnboundedInterval<IntElement> u)
		{
			return Optional.of(u);
		}

		private static Optional<Interval<IntElement>> bounded(final BoundedInterval<IntElement> b)
		{
			return Optional.of(b);
		}
	}

	@Nested
	@DisplayName("when intersecting intervals (general)")
	final class WhenIntersectingIntervalsGeneral
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsCorrectOptionalIntersectionCases")
		@DisplayName("returns correct optional intersection")
		void returnsCorrectOptionalIntersection(final String as, final BoundedInterval<IntElement> left,
		                                        final BoundedInterval<IntElement> right,
		                                        final Optional<BoundedInterval<IntElement>> expected)
		{
			assertThat(left.intersect(right)).as(as).isEqualTo(expected);
			assertThat(right.intersect(left)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsCorrectOptionalIntersectionCases()
		{
			return Stream.of(
					Arguments.of("[1,5] ∩ [3,7] = [3,5]", closed(1, 5), closed(3, 7), Optional.of(closed(3, 5))),
					Arguments.of("[1,5] ∩ [6,7] = empty", closed(1, 5), closed(6, 7), Optional.empty())
			);
		}
	}

	@Nested
	@DisplayName("when checking overlaps")
	final class WhenCheckingOverlaps
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("overlapsCases")
		@DisplayName("returns correct result")
		void returnsCorrectResult(final String as, final Interval<IntElement> a, final Interval<IntElement> b,
		                          final boolean expected)
		{
			assertThat(a.overlaps(b)).as(as).isEqualTo(expected);
			assertThat(b.overlaps(a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> overlapsCases()
		{
			return Stream.of(
					Arguments.of("[1,5] and [3,7] overlap", closed(1, 5), closed(3, 7), true),
					Arguments.of("[1,5] and [5,7] overlap at 5", closed(1, 5), closed(5, 7), true),
					Arguments.of("[1,5) and [5,7] disjoint at 5", closedOpen(1, 5), closed(5, 7), false),
					Arguments.of("[1,3] and [5,7] disjoint", closed(1, 3), closed(5, 7), false),
					Arguments.of("[1,5] and [3,∞) overlap", closed(1, 5), Intervals.atLeast(e(3)), true),
					Arguments.of("[1,5] and [6,∞) disjoint", closed(1, 5), Intervals.atLeast(e(6)), false),
					Arguments.of("[1,5] and (-∞,3] overlap", closed(1, 5), Intervals.atMost(e(3)), true),
					Arguments.of("[1,5] and (-∞,0] disjoint", closed(1, 5), Intervals.atMost(e(0)), false),
					Arguments.of("[1,5] and (-∞,∞) overlap", closed(1, 5), Intervals.all(), true),
					Arguments.of("(-∞,5) and [3,∞) overlap", Intervals.lessThan(e(5)), Intervals.atLeast(e(3)), true),
					Arguments.of("(-∞,5) and [5,∞) disjoint", Intervals.lessThan(e(5)), Intervals.atLeast(e(5)), false),
					Arguments.of("(-∞,5] and [5,∞) overlap at 5", Intervals.atMost(e(5)), Intervals.atLeast(e(5)),
							true),
					Arguments.of("(-∞,∞) overlaps (-∞,∞)", Intervals.all(), Intervals.all(), true)
			);
		}
	}

	@Nested
	@DisplayName("when computing span")
	final class WhenComputingSpan
	{
		@Test
		@DisplayName("span of two bounded intervals is the smallest containing interval")
		void spanOfTwoBoundedIntervalsIsSmallestContainingInterval()
		{
			assertThat(closed(1, 3).span(closed(5, 7))).isEqualTo(closed(1, 7));
			assertThat(closed(1, 5).span(closed(3, 7))).isEqualTo(closed(1, 7));
			assertThat(closedOpen(1, 4).span(closed(2, 6))).isEqualTo(closed(1, 6));
		}

		@Test
		@DisplayName("span with unbounded interval is unbounded")
		void spanWithUnboundedIntervalIsUnbounded()
		{
			Interval<IntElement> result = closed(1, 5).span(Intervals.atLeast(e(3)));
			assertThat(result).isInstanceOf(UnboundedInterval.class);
			assertThat(result.lowerBound()).isPresent();
			assertThat(result.upperBound()).isEmpty();
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("spanOfUnboundedWithBoundedPreservesUnboundednessCases")
		@DisplayName("span of unbounded with bounded preserves unboundedness")
		void spanOfUnboundedWithBoundedPreservesUnboundedness(final String as,
		                                                      final Interval<IntElement> left,
		                                                      final Interval<IntElement> right,
		                                                      final Optional<IntElement> expectedLowerBoundValue,
		                                                      final Optional<IntElement> expectedUpperBoundValue)
		{
			Interval<IntElement> result = left.span(right);

			assertThat(result).as("%s: result type", as).isInstanceOf(UnboundedInterval.class);
			assertThat(result.lowerBound().isPresent()).as("%s: lowerBound presence", as)
			                                           .isEqualTo(expectedLowerBoundValue.isPresent());
			assertThat(result.upperBound().isPresent()).as("%s: upperBound presence", as)
			                                           .isEqualTo(expectedUpperBoundValue.isPresent());

			expectedLowerBoundValue.ifPresent(expected -> assertThat(result.lowerBound().get().value())
					.as("%s: lowerBound value", as)
					.isEqualTo(expected));

			expectedUpperBoundValue.ifPresent(expected -> assertThat(result.upperBound().get().value())
					.as("%s: upperBound value", as)
					.isEqualTo(expected));
		}

		@Test
		@DisplayName("span of two opposite unbounded intervals is all")
		void spanOfTwoOppositeUnboundedIntervalsIsAll()
		{
			Interval<IntElement> result = Intervals.atMost(e(5)).span(Intervals.atLeast(e(3)));
			assertThat(result).isInstanceOf(UnboundedInterval.class);
			assertThat(result.lowerBound()).isEmpty();
			assertThat(result.upperBound()).isEmpty();
		}

		@Test
		@DisplayName("span of two same-side unbounded intervals takes the wider")
		void spanOfTwoSameSideUnboundedIntervalsTakesTheWider()
		{
			Interval<IntElement> result = Intervals.atLeast(e(5)).span(Intervals.atLeast(e(3)));
			assertThat(result.lowerBound()).isPresent();
			assertThat(result.lowerBound().get().value()).isEqualTo(e(3));
			assertThat(result.upperBound()).isEmpty();
		}

		private static Stream<Arguments> spanOfUnboundedWithBoundedPreservesUnboundednessCases()
		{
			return Stream.of(
					Arguments.of("[3,∞) span [1,5] = [1,∞)", Intervals.atLeast(e(3)), closed(1, 5),
							Optional.of(e(1)), Optional.empty()),
					Arguments.of("(-∞,3] span [1,5] = (-∞,5]", Intervals.atMost(e(3)), closed(1, 5),
							Optional.empty(), Optional.of(e(5)))
			);
		}
	}

	@Nested
	@DisplayName("when checking abuts")
	final class WhenCheckingAbuts
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("abutsCases")
		@DisplayName("returns correct result")
		void returnsCorrectResult(final String as, final Interval<IntElement> a, final Interval<IntElement> b,
		                          final boolean expected)
		{
			assertThat(a.abuts(b)).as(as).isEqualTo(expected);
			assertThat(b.abuts(a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> abutsCases()
		{
			return Stream.of(
					Arguments.of("[1,3) abuts [3,5)", closedOpen(1, 3), closedOpen(3, 5), true),
					Arguments.of("[1,3] abuts (3,5)", closed(1, 3), open(3, 5), true),
					Arguments.of("[1,3] and [3,5] overlap not abut", closed(1, 3), closed(3, 5), false),
					Arguments.of("[1,5) abuts [5,∞)", closedOpen(1, 5), Intervals.atLeast(e(5)), true),
					Arguments.of("[1,5] does not abut [5,∞)", closed(1, 5), Intervals.atLeast(e(5)), false),
					Arguments.of("(-∞,3) abuts [3,∞)", Intervals.lessThan(e(3)), Intervals.atLeast(e(3)), true),
					Arguments.of("(-∞,3] abuts (3,∞)", Intervals.atMost(e(3)), Intervals.greaterThan(e(3)), true),
					Arguments.of("(-∞,3) and (3,∞) gap not abut", Intervals.lessThan(e(3)), Intervals.greaterThan(e(3)),
							false),
					Arguments.of("(1,3) and (3,5) gap not abut", open(1, 3), open(3, 5), false)
			);
		}
	}

	@Nested
	@DisplayName("when intervals are built over different orders")
	final class WhenIntervalsAreBuiltOverDifferentOrders
	{
		@Test
		@DisplayName("interval equality ignores the order structure and semantics can differ")
		void intervalEqualityIgnoresOrderStructureAndSemanticsCanDiffer()
		{
			var natural = Intervals.over(IntegerNaturalOrder.INSTANCE).closed(1, 3);
			var absoluteValue = Intervals.over(absoluteValueOrder()).closed(1, 3);

			assertThat(natural).as("equality ignores ordering context").isEqualTo(absoluteValue);
			assertThat(natural.contains(-2)).as("natural order: [-2] is below lower bound 1").isEqualTo(false);
			assertThat(absoluteValue.contains(-2)).as("absolute-value order: [-2] lies between 1 and 3")
			                                      .isEqualTo(true);
		}

		private static TotalOrderStructure<Integer> absoluteValueOrder()
		{
			return (left, right) ->
			{
				int absoluteComparison = Integer.compare(Math.abs(left), Math.abs(right));
				if (absoluteComparison != 0) return OrderRelation.from(absoluteComparison);
				return OrderRelation.from(Integer.compare(left, right));
			};
		}
	}
}
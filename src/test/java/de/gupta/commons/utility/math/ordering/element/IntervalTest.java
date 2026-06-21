package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Interval")
final class IntervalTest
{
	private static Interval<IntElement> closed(final int a, final int b)
	{
		return Intervals.closed(e(a), e(b));
	}

	private static IntElement e(final int value)
	{
		return new IntElement(value);
	}

	private static Interval<IntElement> open(final int a, final int b)
	{
		return Intervals.open(e(a), e(b));
	}

	private static Interval<IntElement> closedOpen(final int a, final int b)
	{
		return Intervals.closedOpen(e(a), e(b));
	}

	private static Interval<IntElement> openClosed(final int a, final int b)
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
	@DisplayName("when constructing")
	final class WhenConstructing
	{
		@Test
		@DisplayName("closed interval with lower > upper is empty")
		void closedIntervalWithLowerGreaterThanUpperIsEmpty()
		{
			assertThat(closed(5, 3).isEmpty()).as("closed(5,3)").isEqualTo(true);
		}

		@Test
		@DisplayName("open interval with equal bounds is empty")
		void openIntervalWithEqualBoundsIsEmpty()
		{
			assertThat(open(3, 3).isEmpty()).as("open(3,3)").isEqualTo(true);
		}

		@Test
		@DisplayName("half-open interval with equal bounds is empty")
		void halfOpenIntervalWithEqualBoundsIsEmpty()
		{
			assertThat(closedOpen(3, 3).isEmpty()).as("closedOpen(3,3)").isEqualTo(true);
			assertThat(openClosed(3, 3).isEmpty()).as("openClosed(3,3)").isEqualTo(true);
		}

		@Test
		@DisplayName("closed interval with equal bounds is a point")
		void closedIntervalWithEqualBoundsIsAPoint()
		{
			assertThat(closed(3, 3).isPoint()).as("closed(3,3).isPoint").isEqualTo(true);
			assertThat(closed(3, 3).isEmpty()).as("closed(3,3).isEmpty").isEqualTo(false);
		}

		@Test
		@DisplayName("empty factory produces an empty interval equal to any other empty")
		void emptyFactoryProducesEmptyIntervalEqualToAnyOtherEmpty()
		{
			assertThat(Intervals.<IntElement>empty()).isEqualTo(closed(5, 3));
			assertThat(Intervals.<IntElement>empty()).isEqualTo(open(3, 3));
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
					Arguments.of("empty contains nothing", Intervals.empty(), 3, false)
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
					Arguments.of("[1,5] and (5,7) disjoint at 5", closed(1, 5), open(5, 7), false),
					Arguments.of("[1,3] and [5,7] disjoint", closed(1, 3), closed(5, 7), false),
					Arguments.of("empty and [1,5] never overlap", Intervals.empty(), closed(1, 5), false),
					Arguments.of("(1,3) and (2,4) overlap", open(1, 3), open(2, 4), true)
			);
		}
	}

	@Nested
	@DisplayName("when intersecting")
	final class WhenIntersecting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intersectCases")
		@DisplayName("produces the correct intersection")
		void producesTheCorrectIntersection(final String as, final Interval<IntElement> a,
		                                    final Interval<IntElement> b, final Interval<IntElement> expected)
		{
			assertThat(a.intersect(b)).as(as).isEqualTo(expected);
			assertThat(b.intersect(a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> intersectCases()
		{
			return Stream.of(
					Arguments.of("[1,5] ∩ [3,7] = [3,5]", closed(1, 5), closed(3, 7), closed(3, 5)),
					Arguments.of("[1,5) ∩ [3,7] = [3,5)", closedOpen(1, 5), closed(3, 7), closedOpen(3, 5)),
					Arguments.of("[1,5] ∩ (5,7] = empty", closed(1, 5), openClosed(5, 7), Intervals.empty()),
					Arguments.of("[1,5] ∩ [5,7] = [5,5]", closed(1, 5), closed(5, 7), closed(5, 5)),
					Arguments.of("[1,3] ∩ [5,7] = empty", closed(1, 3), closed(5, 7), Intervals.empty()),
					Arguments.of("empty ∩ [1,5] = empty", Intervals.empty(), closed(1, 5), Intervals.empty())
			);
		}
	}

	@Nested
	@DisplayName("when checking contains(interval)")
	final class WhenCheckingContainsInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsIntervalCases")
		@DisplayName("returns correct containment result")
		void returnsCorrectContainmentResult(final String as, final Interval<IntElement> outer,
		                                     final Interval<IntElement> inner, final boolean expected)
		{
			assertThat(outer.contains(inner)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsIntervalCases()
		{
			return Stream.of(
					Arguments.of("[1,7] contains [3,5]", closed(1, 7), closed(3, 5), true),
					Arguments.of("[1,7] contains [1,7]", closed(1, 7), closed(1, 7), true),
					Arguments.of("[1,7] contains (1,7)", closed(1, 7), open(1, 7), true),
					Arguments.of("(1,7) does not contain [1,7]", open(1, 7), closed(1, 7), false),
					Arguments.of("[1,5] does not contain [3,7]", closed(1, 5), closed(3, 7), false),
					Arguments.of("any interval contains empty", closed(1, 5), Intervals.empty(), true),
					Arguments.of("empty contains only empty", Intervals.empty(), closed(1, 5), false),
					Arguments.of("empty contains empty", Intervals.empty(), Intervals.empty(), true)
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
					Arguments.of("[1,3) and (3,5] gap not abut", closedOpen(1, 3), openClosed(3, 5), false),
					Arguments.of("[1,2] and [4,5] disjoint not abut", closed(1, 2), closed(4, 5), false)
			);
		}
	}
}
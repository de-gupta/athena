package de.gupta.commons.utility.math.ordering.structure.interval;

import de.gupta.commons.utility.math.ordering.structure.IntegerNaturalOrder;
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

@DisplayName("IntervalOperations (structure side)")
final class IntervalOperationsTest
{
	private static final Intervals<Integer> FACTORY = new Intervals<>(IntegerNaturalOrder.INSTANCE);
	private static final IntervalOperations<Integer> OPS = new IntervalOperations<>(IntegerNaturalOrder.INSTANCE);

	private static BoundedInterval<Integer> closed(final int a, final int b)
	{
		return FACTORY.closed(a, b);
	}

	private static BoundedInterval<Integer> closedOpen(final int a, final int b)
	{
		return FACTORY.closedOpen(a, b);
	}

	@Nested
	@DisplayName("when constructing via factory")
	final class WhenConstructingViaFactory
	{
		@Test
		@DisplayName("throws when lower exceeds upper")
		void throwsWhenLowerExceedsUpper()
		{
			assertThatThrownBy(() -> FACTORY.closed(5, 3)).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> FACTORY.open(3, 3)).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("atLeast and greaterThan produce correct unbounded intervals")
		void unboundedFactoriesProduceCorrectIntervals()
		{
			assertThat(OPS.contains(FACTORY.atLeast(5), 5)).isEqualTo(true);
			assertThat(OPS.contains(FACTORY.atLeast(5), 4)).isEqualTo(false);
			assertThat(OPS.contains(FACTORY.greaterThan(5), 5)).isEqualTo(false);
			assertThat(OPS.contains(FACTORY.greaterThan(5), 6)).isEqualTo(true);
			assertThat(OPS.contains(FACTORY.atMost(5), 5)).isEqualTo(true);
			assertThat(OPS.contains(FACTORY.lessThan(5), 5)).isEqualTo(false);
			assertThat(OPS.contains(FACTORY.all(), Integer.MIN_VALUE)).isEqualTo(true);
		}
	}

	@Nested
	@DisplayName("when checking contains(interval, element)")
	final class WhenCheckingContainsElement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsElementCases")
		@DisplayName("returns correct result")
		void returnsCorrectResult(final String as, final Interval<Integer> interval, final int element,
		                          final boolean expected)
		{
			assertThat(OPS.contains(interval, element)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsElementCases()
		{
			return Stream.of(
					Arguments.of("[1,5] contains 3", closed(1, 5), 3, true),
					Arguments.of("[1,5] excludes 6", closed(1, 5), 6, false),
					Arguments.of("[1,5) excludes 5", closedOpen(1, 5), 5, false),
					Arguments.of("[3,∞) contains 10", FACTORY.atLeast(3), 10, true),
					Arguments.of("[3,∞) excludes 2", FACTORY.atLeast(3), 2, false),
					Arguments.of("(-∞,∞) contains 0", FACTORY.all(), 0, true)
			);
		}
	}

	@Nested
	@DisplayName("when checking contains(outer, inner)")
	final class WhenCheckingContainsInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("containsIntervalCases")
		@DisplayName("returns correct containment result")
		void returnsCorrectContainmentResult(final String as, final Interval<Integer> outer,
		                                     final Interval<Integer> inner, final boolean expected)
		{
			assertThat(OPS.contains(outer, inner)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> containsIntervalCases()
		{
			return Stream.of(
					Arguments.of("[1,7] contains [3,5]", closed(1, 7), closed(3, 5), true),
					Arguments.of("[1,7] contains itself", closed(1, 7), closed(1, 7), true),
					Arguments.of("[1,5] does not contain [3,7]", closed(1, 5), closed(3, 7), false),
					Arguments.of("[1,7] does not contain [1,∞)", closed(1, 7), FACTORY.atLeast(1), false),
					Arguments.of("(-∞,∞) contains [1,5]", FACTORY.all(), closed(1, 5), true),
					Arguments.of("(-∞,∞) contains [1,∞)", FACTORY.all(), FACTORY.atLeast(1), true),
					Arguments.of("[1,∞) contains [3,∞)", FACTORY.atLeast(1), FACTORY.atLeast(3), true),
					Arguments.of("[1,∞) does not contain (-∞,∞)", FACTORY.atLeast(1), FACTORY.all(), false),
					Arguments.of("(-∞,5] contains [1,5]", FACTORY.atMost(5), closed(1, 5), true),
					Arguments.of("(-∞,5) does not contain [1,5]", FACTORY.lessThan(5), closed(1, 5), false)
			);
		}
	}

	@Nested
	@DisplayName("when intersecting")
	final class WhenIntersecting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intersectCases")
		@DisplayName("produces correct optional result")
		void producesCorrectOptionalResult(final String as, final BoundedInterval<Integer> a,
		                                   final BoundedInterval<Integer> b,
		                                   final Optional<BoundedInterval<Integer>> expected)
		{
			assertThat(OPS.intersect(a, b)).as(as).isEqualTo(expected);
			assertThat(OPS.intersect(b, a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> intersectCases()
		{
			return Stream.of(
					Arguments.of("[1,5] ∩ [3,7] = [3,5]", closed(1, 5), closed(3, 7), Optional.of(closed(3, 5))),
					Arguments.of("[1,5] ∩ (5,7] = empty", closed(1, 5), FACTORY.openClosed(5, 7), Optional.empty()),
					Arguments.of("[1,3] ∩ [5,7] = empty", closed(1, 3), closed(5, 7), Optional.empty())
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
		void returnsCorrectResult(final String as, final Interval<Integer> a, final Interval<Integer> b,
		                          final boolean expected)
		{
			assertThat(OPS.overlaps(a, b)).as(as).isEqualTo(expected);
			assertThat(OPS.overlaps(b, a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> overlapsCases()
		{
			return Stream.of(
					Arguments.of("[1,5] and [3,7] overlap", closed(1, 5), closed(3, 7), true),
					Arguments.of("[1,5) and [5,7] disjoint", closedOpen(1, 5), closed(5, 7), false),
					Arguments.of("[1,5] and [3,∞) overlap", closed(1, 5), FACTORY.atLeast(3), true),
					Arguments.of("[1,5] and [6,∞) disjoint", closed(1, 5), FACTORY.atLeast(6), false),
					Arguments.of("(-∞,5) and [5,∞) disjoint", FACTORY.lessThan(5), FACTORY.atLeast(5), false),
					Arguments.of("(-∞,5] and [5,∞) overlap at 5", FACTORY.atMost(5), FACTORY.atLeast(5), true)
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
		void returnsCorrectResult(final String as, final Interval<Integer> a, final Interval<Integer> b,
		                          final boolean expected)
		{
			assertThat(OPS.abuts(a, b)).as(as).isEqualTo(expected);
			assertThat(OPS.abuts(b, a)).as("%s (symmetric)", as).isEqualTo(expected);
		}

		private static Stream<Arguments> abutsCases()
		{
			return Stream.of(
					Arguments.of("[1,3) abuts [3,5)", closedOpen(1, 3), FACTORY.closedOpen(3, 5), true),
					Arguments.of("[1,3] and [3,5] overlap not abut", closed(1, 3), closed(3, 5), false),
					Arguments.of("(-∞,3) abuts [3,∞)", FACTORY.lessThan(3), FACTORY.atLeast(3), true),
					Arguments.of("(-∞,3) and (3,∞) gap", FACTORY.lessThan(3), FACTORY.greaterThan(3), false)
			);
		}
	}

	@Nested
	@DisplayName("when checking isPoint")
	final class WhenCheckingIsPoint
	{
		@Test
		@DisplayName("returns true only for closed intervals with equal bounds")
		void returnsTrueOnlyForClosedIntervalsWithEqualBounds()
		{
			assertThat(OPS.isPoint(closed(3, 3))).isEqualTo(true);
			assertThat(OPS.isPoint(FACTORY.point(7))).isEqualTo(true);
			assertThat(OPS.isPoint(closed(1, 5))).isEqualTo(false);
		}
	}
}
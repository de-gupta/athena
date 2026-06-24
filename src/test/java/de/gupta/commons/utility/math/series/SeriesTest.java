package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.ordering.interval.Intervals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Series")
final class SeriesTest
{
	private static final Series<IntegralNumber, IntegralNumber> SERIES =
			SeriesFactory.of(Map.of(i(1), i(10), i(3), i(30), i(5), i(50), i(7), i(70), i(9), i(90)));

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	@Nested
	@DisplayName("when querying by index")
	final class WhenQueryingByIndex
	{
		@Test
		@DisplayName("at returns value at exact index")
		void atReturnsValueAtExactIndex()
		{
			assertThat(SERIES.at(i(1))).isEqualTo(Optional.of(i(10)));
			assertThat(SERIES.at(i(5))).isEqualTo(Optional.of(i(50)));
			assertThat(SERIES.at(i(9))).isEqualTo(Optional.of(i(90)));
		}

		@Test
		@DisplayName("at returns empty for absent index")
		void atReturnsEmptyForAbsentIndex()
		{
			assertThat(SERIES.at(i(0))).isEmpty();
			assertThat(SERIES.at(i(2))).isEmpty();
			assertThat(SERIES.at(i(10))).isEmpty();
		}
	}

	@Nested
	@DisplayName("when querying boundaries")
	final class WhenQueryingBoundaries
	{
		@Test
		@DisplayName("first returns smallest index entry")
		void firstReturnsSmallestIndexEntry()
		{
			assertThat(SERIES.first()).isPresent()
			                          .hasValueSatisfying(e ->
			                          {
										  assertThat(e.getKey()).isEqualTo(i(1));
										  assertThat(e.getValue()).isEqualTo(i(10));
									  });
		}

		@Test
		@DisplayName("last returns largest index entry")
		void lastReturnsLargestIndexEntry()
		{
			assertThat(SERIES.last()).isPresent()
			                         .hasValueSatisfying(e ->
			                         {
										 assertThat(e.getKey()).isEqualTo(i(9));
										 assertThat(e.getValue()).isEqualTo(i(90));
									 });
		}

		@Test
		@DisplayName("first and last are empty on empty series")
		void firstAndLastAreEmptyOnEmptySeries()
		{
			final Series<IntegralNumber, IntegralNumber> empty = SeriesFactory.empty();
			assertThat(empty.first()).isEmpty();
			assertThat(empty.last()).isEmpty();
		}
	}

	@Nested
	@DisplayName("when slicing with between(T, T)")
	final class WhenSlicingWithBetween
	{
		@Test
		@DisplayName("between uses closed-open semantics [from, to)")
		void betweenUsesClosedOpenSemantics()
		{
			final Series<IntegralNumber, IntegralNumber> slice = SERIES.between(i(3), i(7));
			assertThat(slice.size()).isEqualTo(2);
			assertThat(slice.at(i(3))).isEqualTo(Optional.of(i(30)));
			assertThat(slice.at(i(5))).isEqualTo(Optional.of(i(50)));
			assertThat(slice.at(i(7))).isEmpty();
		}

		@Test
		@DisplayName("between with equal bounds returns empty series")
		void betweenWithEqualBoundsReturnsEmptySeries()
		{
			assertThat(SERIES.between(i(5), i(5)).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("between with no entries in range returns empty series")
		void betweenWithNoEntriesInRangeReturnsEmptySeries()
		{
			assertThat(SERIES.between(i(4), i(5)).isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when slicing with between(Interval)")
	final class WhenSlicingWithInterval
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("intervalSliceCases")
		@DisplayName("respects interval bound semantics")
		void respectsIntervalBoundSemantics(final String as,
		                                    final Series<IntegralNumber, IntegralNumber> slice,
		                                    final int expectedSize,
		                                    final boolean includesThree,
		                                    final boolean includesSeven)
		{
			assertThat(slice.size()).as("%s: size", as).isEqualTo(expectedSize);
			assertThat(slice.at(i(3)).isPresent()).as("%s: includes 3", as).isEqualTo(includesThree);
			assertThat(slice.at(i(7)).isPresent()).as("%s: includes 7", as).isEqualTo(includesSeven);
		}

		@Test
		@DisplayName("unbounded interval returns full series")
		void unboundedIntervalReturnsFullSeries()
		{
			assertThat(SERIES.between(Intervals.all()).size()).isEqualTo(SERIES.size());
		}

		@Test
		@DisplayName("atLeast returns suffix from bound inclusive")
		void atLeastReturnsSuffixFromBoundInclusive()
		{
			final Series<IntegralNumber, IntegralNumber> suffix =
					SERIES.between(Intervals.atLeast(i(5)));
			assertThat(suffix.size()).isEqualTo(3);
			assertThat(suffix.at(i(5))).isPresent();
			assertThat(suffix.at(i(3))).isEmpty();
		}

		@Test
		@DisplayName("atMost returns prefix up to bound inclusive")
		void atMostReturnsPrefixUpToBoundInclusive()
		{
			final Series<IntegralNumber, IntegralNumber> prefix =
					SERIES.between(Intervals.atMost(i(5)));
			assertThat(prefix.size()).isEqualTo(3);
			assertThat(prefix.at(i(5))).isPresent();
			assertThat(prefix.at(i(7))).isEmpty();
		}

		private static Stream<Arguments> intervalSliceCases()
		{
			return Stream.of(
					Arguments.of("[3,7]: both inclusive", SERIES.between(Intervals.closed(i(3), i(7))), 3, true, true),
					Arguments.of("(3,7): both exclusive", SERIES.between(Intervals.open(i(3), i(7))), 1, false, false),
					Arguments.of("[3,7): closed-open", SERIES.between(Intervals.closedOpen(i(3), i(7))), 2, true,
							false),
					Arguments.of("(3,7]: open-closed", SERIES.between(Intervals.openClosed(i(3), i(7))), 2, false, true)
			);
		}
	}

	@Nested
	@DisplayName("when mapping values")
	final class WhenMappingValues
	{
		@Test
		@DisplayName("map transforms each value preserving index")
		void mapTransformsEachValuePreservingIndex()
		{
			final Series<IntegralNumber, IntegralNumber> shifted =
					SERIES.map(e -> e.add(i(5)));
			assertThat(shifted.size()).isEqualTo(SERIES.size());
			assertThat(shifted.at(i(1))).isEqualTo(Optional.of(i(15)));
			assertThat(shifted.at(i(5))).isEqualTo(Optional.of(i(55)));
			assertThat(shifted.at(i(9))).isEqualTo(Optional.of(i(95)));
		}

		@Test
		@DisplayName("map on empty series returns empty series")
		void mapOnEmptySeriesReturnsEmptySeries()
		{
			assertThat(SeriesFactory.<IntegralNumber, IntegralNumber>empty()
			                        .map(e -> e.add(i(1))).isEmpty()).isTrue();
		}
	}

	@Nested
	@DisplayName("when checking size and emptiness")
	final class WhenCheckingSizeAndEmptiness
	{
		@Test
		@DisplayName("size returns entry count")
		void sizeReturnsEntryCount()
		{
			assertThat(SERIES.size()).isEqualTo(5);
			assertThat(SeriesFactory.empty().size()).isEqualTo(0);
		}

		@Test
		@DisplayName("isEmpty distinguishes empty from non-empty")
		void isEmptyDistinguishesEmptyFromNonEmpty()
		{
			assertThat(SERIES.isEmpty()).isFalse();
			assertThat(SeriesFactory.empty().isEmpty()).isTrue();
		}

		@Test
		@DisplayName("indices returns sorted keys")
		void indicesReturnsSortedKeys()
		{
			assertThat(SERIES.indices()).containsExactly(i(1), i(3), i(5), i(7), i(9));
		}
	}
}
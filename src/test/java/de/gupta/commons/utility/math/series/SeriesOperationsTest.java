package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
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

@DisplayName("SeriesOperations")
final class SeriesOperationsTest
{
	private static final Series<IntegralNumber, IntegralNumber> SERIES =
			SeriesFactory.of(Map.of(i(1), i(10), i(3), i(30), i(5), i(50), i(7), i(70), i(9), i(90)));
	private static final Series<IntegralNumber, IntegralNumber> EMPTY = SeriesFactory.empty();

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	@Nested
	@DisplayName("when summing")
	final class WhenSumming
	{
		@Test
		@DisplayName("sum returns total of all values")
		void sumReturnsTotalOfAllValues()
		{
			assertThat(SeriesOperations.sum(SERIES)).isEqualTo(Optional.of(i(250)));
		}

		@Test
		@DisplayName("sum of single-element series returns that element")
		void sumOfSingleElementSeriesReturnsThatElement()
		{
			assertThat(SeriesOperations.sum(SeriesFactory.of(Map.of(i(1), i(42))))).isEqualTo(Optional.of(i(42)));
		}

		@Test
		@DisplayName("sum of empty series returns empty")
		void sumOfEmptySeriesReturnsEmpty()
		{
			assertThat(SeriesOperations.sum(EMPTY)).isEmpty();
		}

		@Test
		@DisplayName("sum of sub-series respects the slice")
		void sumOfSubSeriesRespectsTheSlice()
		{
			assertThat(SeriesOperations.sum(SERIES.between(i(3), i(8))))
					.isEqualTo(Optional.of(i(150)));
		}

		@Test
		@DisplayName("sum of two-element series")
		void sumOfTwoElementSeries()
		{
			assertThat(SeriesOperations.sum(SeriesFactory.of(Map.of(i(1), i(7), i(2), i(3)))))
					.isEqualTo(Optional.of(i(10)));
		}

		@Test
		@DisplayName("sum with negative values")
		void sumWithNegativeValues()
		{
			final Series<IntegralNumber, IntegralNumber> mixed =
					SeriesFactory.of(Map.of(i(1), i(10), i(2), i(-10), i(3), i(5)));
			assertThat(SeriesOperations.sum(mixed)).isEqualTo(Optional.of(i(5)));
		}

		@Test
		@DisplayName("sum of all-negative series")
		void sumOfAllNegativeSeries()
		{
			final Series<IntegralNumber, IntegralNumber> negative =
					SeriesFactory.of(Map.of(i(1), i(-10), i(2), i(-20), i(3), i(-30)));
			assertThat(SeriesOperations.sum(negative)).isEqualTo(Optional.of(i(-60)));
		}
	}

	@Nested
	@DisplayName("when averaging")
	final class WhenAveraging
	{
		@Test
		@DisplayName("average returns sum divided by count (floor)")
		void averageReturnsSumDividedByCountFloor()
		{
			assertThat(SeriesOperations.average(SERIES, RoundingStrategies.floor()))
					.isEqualTo(Optional.of(i(50)));
		}

		@Test
		@DisplayName("average of series with non-divisible sum floors correctly")
		void averageWithNonDivisibleSumFloorsCorrectly()
		{
			final Series<IntegralNumber, IntegralNumber> uneven =
					SeriesFactory.of(Map.of(i(1), i(10), i(2), i(20), i(3), i(31)));
			assertThat(SeriesOperations.average(uneven, RoundingStrategies.floor()))
					.isEqualTo(Optional.of(i(20)));
		}

		@Test
		@DisplayName("average of empty series returns empty")
		void averageOfEmptySeriesReturnsEmpty()
		{
			assertThat(SeriesOperations.average(EMPTY, RoundingStrategies.floor())).isEmpty();
		}

		@Test
		@DisplayName("average of single element returns that element")
		void averageOfSingleElementReturnsThatElement()
		{
			assertThat(SeriesOperations.average(SeriesFactory.of(Map.of(i(1), i(42))), RoundingStrategies.floor()))
					.isEqualTo(Optional.of(i(42)));
		}

		@Test
		@DisplayName("average of two-element series")
		void averageOfTwoElementSeries()
		{
			final Series<IntegralNumber, IntegralNumber> two =
					SeriesFactory.of(Map.of(i(1), i(10), i(2), i(20)));
			assertThat(SeriesOperations.average(two, RoundingStrategies.floor()))
					.isEqualTo(Optional.of(i(15)));
		}

		@Test
		@DisplayName("ceiling rounds up for non-divisible sum")
		void ceilingRoundsUpForNonDivisibleSum()
		{
			final Series<IntegralNumber, IntegralNumber> uneven =
					SeriesFactory.of(Map.of(i(1), i(10), i(2), i(20), i(3), i(31)));
			assertThat(SeriesOperations.average(uneven, RoundingStrategies.ceiling()))
					.isEqualTo(Optional.of(i(21)));
		}

		@Test
		@DisplayName("average respects sub-series slice")
		void averageRespectsSubSeriesSlice()
		{
			assertThat(SeriesOperations.average(SERIES.between(i(3), i(8)), RoundingStrategies.floor()))
					.isEqualTo(Optional.of(i(50)));
		}
	}

	@Nested
	@DisplayName("when computing changes")
	final class WhenComputingChanges
	{
		@Test
		@DisplayName("changes returns consecutive differences indexed at later entry")
		void changesReturnsConsecutiveDifferences()
		{
			final Series<IntegralNumber, IntegralNumber> changes =
					SeriesOperations.changes(SERIES);
			assertThat(changes.size()).isEqualTo(4);
			assertThat(changes.at(i(1))).isEmpty();
			assertThat(changes.at(i(3))).isEqualTo(Optional.of(i(20)));
			assertThat(changes.at(i(5))).isEqualTo(Optional.of(i(20)));
			assertThat(changes.at(i(7))).isEqualTo(Optional.of(i(20)));
			assertThat(changes.at(i(9))).isEqualTo(Optional.of(i(20)));
		}

		@Test
		@DisplayName("changes of decreasing series returns negative differences")
		void changesOfDecreasingSeriesReturnsNegativeDifferences()
		{
			final Series<IntegralNumber, IntegralNumber> decreasing =
					SeriesFactory.of(Map.of(i(1), i(90), i(2), i(60), i(3), i(30)));
			final Series<IntegralNumber, IntegralNumber> changes =
					SeriesOperations.changes(decreasing);
			assertThat(changes.at(i(2))).isEqualTo(Optional.of(i(-30)));
			assertThat(changes.at(i(3))).isEqualTo(Optional.of(i(-30)));
		}

		@Test
		@DisplayName("changes of single-element series returns empty series")
		void changesOfSingleElementSeriesReturnsEmptySeries()
		{
			assertThat(SeriesOperations.changes(SeriesFactory.of(Map.of(i(1), i(99)))).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("changes of empty series returns empty series")
		void changesOfEmptySeriesReturnsEmptySeries()
		{
			assertThat(SeriesOperations.changes(EMPTY).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("changes respects sub-series slice")
		void changesRespectsSubSeriesSlice()
		{
			final Series<IntegralNumber, IntegralNumber> changes =
					SeriesOperations.changes(SERIES.between(i(3), i(8)));
			assertThat(changes.size()).isEqualTo(2);
			assertThat(changes.at(i(5))).isEqualTo(Optional.of(i(20)));
			assertThat(changes.at(i(7))).isEqualTo(Optional.of(i(20)));
		}

		@Test
		@DisplayName("changes of two-element series returns one entry")
		void changesOfTwoElementSeriesReturnsOneEntry()
		{
			final Series<IntegralNumber, IntegralNumber> two =
					SeriesFactory.of(Map.of(i(1), i(10), i(2), i(30)));
			final Series<IntegralNumber, IntegralNumber> changes = SeriesOperations.changes(two);
			assertThat(changes.size()).isEqualTo(1);
			assertThat(changes.at(i(2))).isEqualTo(Optional.of(i(20)));
		}

		@Test
		@DisplayName("flat series produces all-zero changes")
		void flatSeriesProducesAllZeroChanges()
		{
			final Series<IntegralNumber, IntegralNumber> flat =
					SeriesFactory.of(Map.of(i(1), i(42), i(2), i(42), i(3), i(42)));
			final Series<IntegralNumber, IntegralNumber> changes = SeriesOperations.changes(flat);
			assertThat(changes.at(i(2))).isEqualTo(Optional.of(i(0)));
			assertThat(changes.at(i(3))).isEqualTo(Optional.of(i(0)));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("changesCases")
		@DisplayName("produces correct consecutive differences")
		void producesCorrectConsecutiveDifferences(final String as,
		                                           final Series<IntegralNumber, IntegralNumber> series,
		                                           final IntegralNumber index,
		                                           final IntegralNumber expected)
		{
			assertThat(SeriesOperations.changes(series).at(index)).as(as).isEqualTo(Optional.of(expected));
		}

		@Test
		@DisplayName("changes has exactly size-1 entries")
		void changesHasExactlySizeMinusOneEntries()
		{
			assertThat(SeriesOperations.changes(SERIES).size()).isEqualTo(SERIES.size() - 1);
		}

		private static Stream<Arguments> changesCases()
		{
			return Stream.of(
					Arguments.of("alternating up: 10→30", SeriesFactory.of(
							Map.of(i(1), i(10), i(2), i(30), i(3), i(20), i(4), i(40))), i(2), i(20)),
					Arguments.of("alternating down: 30→20", SeriesFactory.of(
							Map.of(i(1), i(10), i(2), i(30), i(3), i(20), i(4), i(40))), i(3), i(-10)),
					Arguments.of("large jump", SeriesFactory.of(
							Map.of(i(1), i(1), i(2), i(1000))), i(2), i(999)),
					Arguments.of("negative to positive", SeriesFactory.of(
							Map.of(i(1), i(-50), i(2), i(50))), i(2), i(100)),
					Arguments.of("zero to value", SeriesFactory.of(
							Map.of(i(1), i(0), i(2), i(7))), i(2), i(7))
			);
		}
	}

	@Nested
	@DisplayName("when computing ratios")
	final class WhenComputingRatios
	{
		private static final Series<IntegralNumber, RationalNumber> DOUBLING =
				SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(2, 1), i(3), r(4, 1)));
		private static final Series<IntegralNumber, RationalNumber> HALVING =
				SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(1, 2), i(3), r(1, 4)));

		@Test
		@DisplayName("returns consecutive ratios indexed at the later entry")
		void returnsConsecutiveRatios()
		{
			final Series<IntegralNumber, RationalNumber> result = SeriesOperations.ratios(DOUBLING);
			assertThat(result.size()).isEqualTo(2);
			assertThat(result.at(i(1))).isEmpty();
			assertThat(result.at(i(2))).isEqualTo(Optional.of(r(2, 1)));
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(2, 1)));
		}

		private static RationalNumber r(final long num, final long denom)
		{
			return RationalNumberFactory.of(num, denom);
		}

		@Test
		@DisplayName("ratio is normalised")
		void ratioIsNormalised()
		{
			final Series<IntegralNumber, RationalNumber> result = SeriesOperations.ratios(HALVING);
			assertThat(result.at(i(2))).isEqualTo(Optional.of(r(1, 2)));
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(1, 2)));
		}

		@Test
		@DisplayName("mixed ratio")
		void mixedRatio()
		{
			final Series<IntegralNumber, RationalNumber> mixed =
					SeriesFactory.of(Map.of(i(1), r(1, 2), i(2), r(3, 4)));
			assertThat(SeriesOperations.ratios(mixed).at(i(2))).isEqualTo(Optional.of(r(3, 2)));
		}

		@Test
		@DisplayName("single element returns empty ratios")
		void singleElementReturnsEmptyRatios()
		{
			assertThat(SeriesOperations.ratios(SeriesFactory.of(Map.of(i(1), r(1, 1)))).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("empty series returns empty ratios")
		void emptySeriesReturnsEmptyRatios()
		{
			assertThat(
					SeriesOperations.ratios(SeriesFactory.<IntegralNumber, RationalNumber>empty()).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("equal consecutive elements produce ratio of one")
		void equalConsecutiveElementsProduceRatioOfOne()
		{
			final Series<IntegralNumber, RationalNumber> flat =
					SeriesFactory.of(Map.of(i(1), r(3, 7), i(2), r(3, 7), i(3), r(3, 7)));
			final Series<IntegralNumber, RationalNumber> ratios = SeriesOperations.ratios(flat);
			assertThat(ratios.at(i(2))).isEqualTo(Optional.of(r(1, 1)));
			assertThat(ratios.at(i(3))).isEqualTo(Optional.of(r(1, 1)));
		}

		@Test
		@DisplayName("two-element series returns one ratio")
		void twoElementSeriesReturnsOneRatio()
		{
			final Series<IntegralNumber, RationalNumber> two =
					SeriesFactory.of(Map.of(i(1), r(1, 2), i(2), r(3, 4)));
			final Series<IntegralNumber, RationalNumber> ratios = SeriesOperations.ratios(two);
			assertThat(ratios.size()).isEqualTo(1);
			assertThat(ratios.at(i(2))).isEqualTo(Optional.of(r(3, 2)));
		}

		@Test
		@DisplayName("ratios has exactly size-1 entries")
		void ratiosHasExactlySizeMinusOneEntries()
		{
			assertThat(SeriesOperations.ratios(DOUBLING).size()).isEqualTo(DOUBLING.size() - 1);
		}

		@Test
		@DisplayName("ratios respects sub-series slice")
		void ratiosRespectsSubSeriesSlice()
		{
			final Series<IntegralNumber, RationalNumber> growing =
					SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(2, 1), i(3), r(4, 1), i(4), r(8, 1)));
			final Series<IntegralNumber, RationalNumber> ratios =
					SeriesOperations.ratios(growing.between(i(2), i(4)));
			assertThat(ratios.size()).isEqualTo(1);
			assertThat(ratios.at(i(3))).isEqualTo(Optional.of(r(2, 1)));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("ratioCases")
		@DisplayName("produces correct consecutive ratios")
		void producesCorrectConsecutiveRatios(final String as,
		                                      final Series<IntegralNumber, RationalNumber> series,
		                                      final IntegralNumber index,
		                                      final RationalNumber expected)
		{
			assertThat(SeriesOperations.ratios(series).at(index)).as(as).isEqualTo(Optional.of(expected));
		}

		private static Stream<Arguments> ratioCases()
		{
			return Stream.of(
					Arguments.of("tripling: 1→3", SeriesFactory.of(
							Map.of(i(1), r(1, 1), i(2), r(3, 1))), i(2), r(3, 1)),
					Arguments.of("quarter: 1→1/4", SeriesFactory.of(
							Map.of(i(1), r(1, 1), i(2), r(1, 4))), i(2), r(1, 4)),
					Arguments.of("non-uniform: 2/3→3/4", SeriesFactory.of(
							Map.of(i(1), r(2, 3), i(2), r(3, 4))), i(2), r(9, 8)),
					Arguments.of("normalises: 6/4 = 3/2", SeriesFactory.of(
							Map.of(i(1), r(2, 1), i(2), r(3, 1))), i(2), r(3, 2)),
					Arguments.of("less than one: 3→2", SeriesFactory.of(
							Map.of(i(1), r(3, 1), i(2), r(2, 1))), i(2), r(2, 3))
			);
		}
	}

	@Nested
	@DisplayName("when computing percentage changes")
	final class WhenComputingPercentageChanges
	{
		private static final Series<IntegralNumber, RationalNumber> DOUBLING =
				SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(2, 1), i(3), r(4, 1)));
		private static final Series<IntegralNumber, RationalNumber> HALVING =
				SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(1, 2), i(3), r(1, 4)));

		@Test
		@DisplayName("doubling series yields 100% change each step")
		void doublingSeriesYields100PercentChange()
		{
			final Series<IntegralNumber, RationalNumber> result = SeriesOperations.percentageChanges(DOUBLING);
			assertThat(result.at(i(2))).isEqualTo(Optional.of(r(1, 1)));
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(1, 1)));
		}

		private static RationalNumber r(final long num, final long denom)
		{
			return RationalNumberFactory.of(num, denom);
		}

		@Test
		@DisplayName("halving series yields -50% change each step")
		void halvingSeriesYieldsMinus50PercentChange()
		{
			final Series<IntegralNumber, RationalNumber> result = SeriesOperations.percentageChanges(HALVING);
			assertThat(result.at(i(2))).isEqualTo(Optional.of(r(-1, 2)));
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(-1, 2)));
		}

		@Test
		@DisplayName("flat series yields 0% change")
		void flatSeriesYieldsZeroPercentChange()
		{
			final Series<IntegralNumber, RationalNumber> flat =
					SeriesFactory.of(Map.of(i(1), r(3, 4), i(2), r(3, 4), i(3), r(3, 4)));
			final Series<IntegralNumber, RationalNumber> result = SeriesOperations.percentageChanges(flat);
			assertThat(result.at(i(2))).isEqualTo(Optional.of(r(0, 1)));
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(0, 1)));
		}

		@Test
		@DisplayName("empty series returns empty percentage changes")
		void emptySeriesReturnsEmpty()
		{
			assertThat(SeriesOperations.percentageChanges(
					SeriesFactory.<IntegralNumber, RationalNumber>empty()).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("single element returns empty percentage changes")
		void singleElementReturnsEmpty()
		{
			assertThat(SeriesOperations.percentageChanges(
					SeriesFactory.of(Map.of(i(1), r(1, 1)))).isEmpty()).isTrue();
		}

		@Test
		@DisplayName("two-element series returns one percentage change")
		void twoElementSeriesReturnsOnePercentageChange()
		{
			final Series<IntegralNumber, RationalNumber> two =
					SeriesFactory.of(Map.of(i(1), r(4, 1), i(2), r(5, 1)));
			assertThat(SeriesOperations.percentageChanges(two).at(i(2)))
					.isEqualTo(Optional.of(r(1, 4)));
		}

		@Test
		@DisplayName("percentage changes respects sub-series slice")
		void percentageChangesRespectsSubSeriesSlice()
		{
			final Series<IntegralNumber, RationalNumber> growing =
					SeriesFactory.of(Map.of(i(1), r(1, 1), i(2), r(2, 1), i(3), r(4, 1), i(4), r(8, 1)));
			final Series<IntegralNumber, RationalNumber> result =
					SeriesOperations.percentageChanges(growing.between(i(2), i(4)));
			assertThat(result.size()).isEqualTo(1);
			assertThat(result.at(i(3))).isEqualTo(Optional.of(r(1, 1)));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("percentageChangeCases")
		@DisplayName("produces correct percentage change")
		void producesCorrectPercentageChange(final String as,
		                                     final RationalNumber from,
		                                     final RationalNumber to,
		                                     final RationalNumber expected)
		{
			final Series<IntegralNumber, RationalNumber> series =
					SeriesFactory.of(Map.of(i(1), from, i(2), to));
			assertThat(SeriesOperations.percentageChanges(series).at(i(2)))
					.as(as).isEqualTo(Optional.of(expected));
		}

		private static Stream<Arguments> percentageChangeCases()
		{
			return Stream.of(
					Arguments.of("+25%: 4→5", r(4, 1), r(5, 1), r(1, 4)),
					Arguments.of("-25%: 4→3", r(4, 1), r(3, 1), r(-1, 4)),
					Arguments.of("+50%: 2/3→1", r(2, 3), r(1, 1), r(1, 2)),
					Arguments.of("+200%: 1→3", r(1, 1), r(3, 1), r(2, 1)),
					Arguments.of("-100% impossible: divides to -1 is fine", r(2, 1), r(1, 1), r(-1, 2)),
					Arguments.of("unchanged: same value", r(5, 7), r(5, 7), r(0, 1))
			);
		}
	}
}
package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SeriesOperations")
final class SeriesOperationsTest
{
	private static final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> SERIES =
			SeriesFactory.of(Map.of(i(1), i(10), i(3), i(30), i(5), i(50), i(7), i(70), i(9), i(90)));
	private static final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> EMPTY = SeriesFactory.empty();

	private static IntegersAsEuclideanDomain i(final long v)
	{
		return IntegersAsEuclideanDomain.of(v);
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
			final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> uneven =
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
	}

	@Nested
	@DisplayName("when computing changes")
	final class WhenComputingChanges
	{
		@Test
		@DisplayName("changes returns consecutive differences indexed at later entry")
		void changesReturnsConsecutiveDifferences()
		{
			final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> changes =
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
			final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> decreasing =
					SeriesFactory.of(Map.of(i(1), i(90), i(2), i(60), i(3), i(30)));
			final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> changes =
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
			final Series<IntegersAsEuclideanDomain, IntegersAsEuclideanDomain> changes =
					SeriesOperations.changes(SERIES.between(i(3), i(8)));
			assertThat(changes.size()).isEqualTo(2);
			assertThat(changes.at(i(5))).isEqualTo(Optional.of(i(20)));
			assertThat(changes.at(i(7))).isEqualTo(Optional.of(i(20)));
		}
	}
}
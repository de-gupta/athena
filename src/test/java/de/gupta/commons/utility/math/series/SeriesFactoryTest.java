package de.gupta.commons.utility.math.series;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import de.gupta.commons.utility.math.ordering.structure.IntegerNaturalOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SeriesFactory")
final class SeriesFactoryTest
{
	private static IntegralNumbers i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	@Nested
	@DisplayName("when constructing via element-side factory")
	final class WhenConstructingViaElementSideFactory
	{
		@Test
		@DisplayName("of() produces series sorted by T's natural order regardless of insertion order")
		void ofProducesSeriesSortedByNaturalOrder()
		{
			final Series<IntegralNumbers, IntegralNumbers> series =
					SeriesFactory.of(Map.of(i(5), i(50), i(1), i(10), i(3), i(30)));
			assertThat(series.indices()).containsExactly(i(1), i(3), i(5));
			assertThat(series.first()).isPresent()
			                          .hasValueSatisfying(e -> assertThat(e.getKey()).isEqualTo(i(1)));
			assertThat(series.last()).isPresent()
			                         .hasValueSatisfying(e -> assertThat(e.getKey()).isEqualTo(i(5)));
		}

		@Test
		@DisplayName("of() preserves all entries")
		void ofPreservesAllEntries()
		{
			final Series<IntegralNumbers, IntegralNumbers> series =
					SeriesFactory.of(Map.of(i(1), i(10), i(3), i(30), i(5), i(50)));
			assertThat(series.size()).isEqualTo(3);
			assertThat(series.at(i(1))).isEqualTo(Optional.of(i(10)));
			assertThat(series.at(i(3))).isEqualTo(Optional.of(i(30)));
			assertThat(series.at(i(5))).isEqualTo(Optional.of(i(50)));
		}

		@Test
		@DisplayName("empty() produces an empty series")
		void emptyProducesEmptySeries()
		{
			final Series<IntegralNumbers, IntegralNumbers> empty = SeriesFactory.empty();
			assertThat(empty.isEmpty()).isTrue();
			assertThat(empty.size()).isEqualTo(0);
			assertThat(empty.first()).isEmpty();
			assertThat(empty.last()).isEmpty();
		}
	}

	@Nested
	@DisplayName("when constructing via structure-side factory")
	final class WhenConstructingViaStructureSideFactory
	{
		@Test
		@DisplayName("over(order).of() produces correctly ordered series")
		void overOrderOfProducesCorrectlyOrderedSeries()
		{
			final Series<Integer, String> series = SeriesFactory.over(IntegerNaturalOrder.INSTANCE)
			                                                    .of(Map.of(3, "three", 1, "one", 2, "two"));
			assertThat(series.indices()).containsExactly(1, 2, 3);
			assertThat(series.first()).isPresent()
			                          .hasValueSatisfying(e -> assertThat(e.getKey()).isEqualTo(1));
			assertThat(series.last()).isPresent()
			                         .hasValueSatisfying(e -> assertThat(e.getKey()).isEqualTo(3));
			assertThat(series.at(2)).isEqualTo(Optional.of("two"));
		}

		@Test
		@DisplayName("over(order).empty() produces an empty series")
		void overOrderEmptyProducesEmptySeries()
		{
			final Series<Integer, String> empty = SeriesFactory.over(IntegerNaturalOrder.INSTANCE).empty();
			assertThat(empty.isEmpty()).isTrue();
			assertThat(empty.first()).isEmpty();
		}

		@Test
		@DisplayName("over(order) supports types not in the TotallyOrdered hierarchy")
		void overOrderSupportsExternalTypes()
		{
			final Series<Integer, IntegralNumbers> series =
					SeriesFactory.over(IntegerNaturalOrder.INSTANCE)
					             .of(Map.of(10, i(100), 30, i(300), 20, i(200)));
			assertThat(series.indices()).containsExactly(10, 20, 30);
			assertThat(series.at(20)).isEqualTo(Optional.of(i(200)));
		}
	}
}
package de.gupta.commons.utility.math.interpolation.data;

import de.gupta.aletheia.collection.Dyad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@DisplayName("InterpolationData")
final class InterpolationDataTest
{
	private static final Sample<Double, String> S1 = Sample.of(1.0, "a");
	private static final Sample<Double, String> S3 = Sample.of(3.0, "b");
	private static final Sample<Double, String> S5 = Sample.of(5.0, "c");
	private static final Sample<Double, String> S7 = Sample.of(7.0, "d");

	private static InterpolationData<Double, String> fourKnots()
	{
		return InterpolationData.of(List.of(S1, S3, S5, S7), Double::compare);
	}

	@Nested
	@DisplayName("when samples() is called")
	final class WhenSamplesQueried
	{
		@Test
		@DisplayName("returns samples sorted ascending by X regardless of insertion order")
		void returnsSamplesSortedAscending()
		{
			InterpolationData<Double, String> data = InterpolationData.of(
					List.of(S7, S1, S5, S3), Double::compare);

			assertThat(data.samples())
					.as("samples must be sorted ascending by X")
					.containsExactly(S1, S3, S5, S7);
		}

		@Test
		@DisplayName("returns an unmodifiable view")
		void returnsUnmodifiableList()
		{
			assertThat(fourKnots().samples())
					.as("samples list must be unmodifiable")
					.isUnmodifiable();
		}
	}

	@Nested
	@DisplayName("when query is interior")
	final class WhenQueryIsInterior
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsStraddlingPairCases")
		@DisplayName("returns the pair of samples straddling the query")
		void returnsStraddlingPair(final String as, final double query,
		                           final Dyad<Sample<Double, String>, Sample<Double, String>> expected)
		{
			Optional<Dyad<Sample<Double, String>, Sample<Double, String>>> result = fourKnots().bracket(query);

			assertThat(result).as(as).contains(expected);
		}

		private static Stream<Arguments> returnsStraddlingPairCases()
		{
			return Stream.of(
					Arguments.of("query between first and second knot", 2.0, Dyad.of(S1, S3)),
					Arguments.of("query between second and third knot", 4.0, Dyad.of(S3, S5)),
					Arguments.of("query between third and fourth knot", 6.0, Dyad.of(S5, S7))
			);
		}
	}

	@Nested
	@DisplayName("when query hits a knot exactly")
	final class WhenQueryHitsKnotExactly
	{
		@Test
		@DisplayName("hit on first knot returns first pair")
		void hitOnFirstKnotReturnsFirstPair()
		{
			assertThat(fourKnots().bracket(1.0))
					.as("exact hit on first knot must return first pair")
					.contains(Dyad.of(S1, S3));
		}

		@Test
		@DisplayName("hit on last knot returns last pair")
		void hitOnLastKnotReturnsLastPair()
		{
			assertThat(fourKnots().bracket(7.0))
					.as("exact hit on last knot must return last pair")
					.contains(Dyad.of(S5, S7));
		}

		@Test
		@DisplayName("hit on interior knot returns that knot as left of its rightward pair")
		void hitOnInteriorKnotReturnsLeftBiasedPair()
		{
			assertThat(fourKnots().bracket(3.0))
					.as("exact hit on interior knot must return leftward pair")
					.contains(Dyad.of(S3, S5));
		}
	}

	@Nested
	@DisplayName("when query is out of range")
	final class WhenQueryIsOutOfRange
	{
		@Test
		@DisplayName("query below minimum returns the leftmost pair")
		void queryBelowMinimumReturnsLeftmostPair()
		{
			assertThat(fourKnots().bracket(-99.0))
					.as("query below minimum must return leftmost pair")
					.contains(Dyad.of(S1, S3));
		}

		@Test
		@DisplayName("query above maximum returns the rightmost pair")
		void queryAboveMaximumReturnsRightmostPair()
		{
			assertThat(fourKnots().bracket(99.0))
					.as("query above maximum must return rightmost pair")
					.contains(Dyad.of(S5, S7));
		}
	}

	@Nested
	@DisplayName("when data is minimal")
	final class WhenDataIsMinimal
	{
		@Test
		@DisplayName("two knots always returns that pair regardless of query")
		void twoKnotsAlwaysReturnsThePair()
		{
			InterpolationData<Double, String> data = InterpolationData.of(List.of(S1, S7), Double::compare);

			assertThat(data.bracket(0.0)).as("two-knot data must return the only pair for any query")
			                             .contains(Dyad.of(S1, S7));
			assertThat(data.bracket(4.0)).as("two-knot data must return the only pair for any query")
			                             .contains(Dyad.of(S1, S7));
			assertThat(data.bracket(99.0)).as("two-knot data must return the only pair for any query")
			                              .contains(Dyad.of(S1, S7));
		}

		@Test
		@DisplayName("single knot returns empty")
		void singleKnotReturnsEmpty()
		{
			InterpolationData<Double, String> data = InterpolationData.of(List.of(S1), Double::compare);

			assertThat(data.bracket(1.0))
					.as("single-knot data must return empty")
					.isEmpty();
		}

		@Test
		@DisplayName("empty data returns empty")
		void emptyDataReturnsEmpty()
		{
			InterpolationData<Double, String> data = InterpolationData.of(List.of(), Double::compare);

			assertThat(data.bracket(0.0))
					.as("empty data must return empty")
					.isEmpty();
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("null samples list throws NullPointerException")
		void nullSamplesThrows()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> InterpolationData.of(null, Double::compare))
					.withMessage("samples");
		}

		@Test
		@DisplayName("null comparator throws NullPointerException")
		void nullComparatorThrows()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> InterpolationData.of(List.of(S1), null))
					.withMessage("order");
		}

		@Test
		@DisplayName("null element in samples list throws NullPointerException")
		void nullSampleElementThrows()
		{
			List<Sample<Double, String>> samples = new ArrayList<>();
			samples.add(S1);
			samples.add(null);
			assertThatNullPointerException()
					.isThrownBy(() -> InterpolationData.of(samples, Double::compare));
		}

		@Test
		@DisplayName("null query throws NullPointerException")
		void nullQueryThrows()
		{
			assertThatNullPointerException()
					.isThrownBy(() -> fourKnots().bracket(null))
					.withMessage("query");
		}
	}
}
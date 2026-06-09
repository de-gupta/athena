package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.analysis.space.standard.DoubleInterpolatableSpace;
import de.gupta.commons.utility.math.analysis.space.standard.DoubleVectorSpaceStructure;
import de.gupta.commons.utility.math.interpolation.TieBreakingPolicy;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("NearestNeighborMethod#fit")
final class NearestNeighborMethodTest
{
	private static final NearestNeighborMethod<Double, Double> LOWER_METHOD =
			NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR, TieBreakingPolicy.LOWER);

	private static InterpolationData<Double, Double> dataOf(final double... pairs)
	{
		List<Sample<Double, Double>> samples = IntStream.iterate(0, i -> i + 2)
		                                                .limit(pairs.length / 2)
		                                                .mapToObj(i -> Sample.of(pairs[i], pairs[i + 1]))
		                                                .toList();
		return InterpolationData.of(samples, Double::compare);
	}

	@Nested
	@DisplayName("when data has a single knot")
	final class WhenDataHasSingleKnot
	{
		@Test
		@DisplayName("returns the only value for any query")
		void returnsTheOnlyValueForAnyQuery()
		{
			InterpolationData<Double, Double> data = dataOf(5.0, 42.0);

			assertThat(LOWER_METHOD.fit(data).interpolate(0.0))
					.as("query left of single knot must return the knot value")
					.isEqualTo(42.0);
			assertThat(LOWER_METHOD.fit(data).interpolate(5.0))
					.as("query at single knot must return the knot value")
					.isEqualTo(42.0);
			assertThat(LOWER_METHOD.fit(data).interpolate(99.0))
					.as("query right of single knot must return the knot value")
					.isEqualTo(42.0);
		}
	}

	@Nested
	@DisplayName("when query matches a knot exactly")
	final class WhenQueryMatchesKnotExactly
	{
		@Test
		@DisplayName("returns the exact knot value")
		void returnsExactKnotValue()
		{
			InterpolationData<Double, Double> data = dataOf(1.0, 10.0, 3.0, 20.0, 5.0, 30.0);

			assertThat(LOWER_METHOD.fit(data).interpolate(3.0))
					.as("exact knot match must return that knot's Y value")
					.isEqualTo(20.0);
		}
	}

	@Nested
	@DisplayName("when nearest is unambiguous")
	final class WhenNearestIsUnambiguous
	{
		@Test
		@DisplayName("returns the value of the nearest knot for an interior query")
		void returnsNearestKnotValueForInteriorQuery()
		{
			InterpolationData<Double, Double> data = dataOf(0.0, 10.0, 10.0, 20.0);

			assertThat(LOWER_METHOD.fit(data).interpolate(3.0))
					.as("query closer to x=0 must return y=10")
					.isEqualTo(10.0);
			assertThat(LOWER_METHOD.fit(data).interpolate(7.0))
					.as("query closer to x=10 must return y=20")
					.isEqualTo(20.0);
		}

		@Test
		@DisplayName("returns the nearest boundary value for an extrapolated query")
		void returnsNearestBoundaryValueForExtrapolatedQuery()
		{
			InterpolationData<Double, Double> data = dataOf(1.0, 10.0, 3.0, 20.0, 5.0, 30.0);

			assertThat(LOWER_METHOD.fit(data).interpolate(-99.0))
					.as("query far left must return leftmost knot value")
					.isEqualTo(10.0);
			assertThat(LOWER_METHOD.fit(data).interpolate(99.0))
					.as("query far right must return rightmost knot value")
					.isEqualTo(30.0);
		}
	}

	@Nested
	@DisplayName("when two knots are equidistant from the query")
	final class WhenTwoKnotsAreEquidistant
	{
		private final InterpolationData<Double, Double> symmetricData = dataOf(0.0, 10.0, 2.0, 30.0);

		@Test
		@DisplayName("LOWER policy returns the value of the knot with lower X")
		void lowerPolicyReturnsLowerXValue()
		{
			NearestNeighborMethod<Double, Double> method =
					NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR, TieBreakingPolicy.LOWER);

			assertThat(method.fit(symmetricData).interpolate(1.0))
					.as("LOWER must return the value at x=0 when equidistant from x=0 and x=2")
					.isEqualTo(10.0);
		}

		@Test
		@DisplayName("UPPER policy returns the value of the knot with higher X")
		void upperPolicyReturnsUpperXValue()
		{
			NearestNeighborMethod<Double, Double> method =
					NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR, TieBreakingPolicy.UPPER);

			assertThat(method.fit(symmetricData).interpolate(1.0))
					.as("UPPER must return the value at x=2 when equidistant from x=0 and x=2")
					.isEqualTo(30.0);
		}

		@Test
		@DisplayName("AVERAGE policy returns the midpoint of the two equidistant values")
		void averagePolicyReturnsMidpoint()
		{
			NearestNeighborMethod<Double, Double> method =
					NearestNeighborMethod.averaging(DoubleInterpolatableSpace.LINEAR,
							DoubleVectorSpaceStructure.INSTANCE);

			assertThat(method.fit(symmetricData).interpolate(1.0))
					.as("AVERAGE must return midpoint of y=10 and y=30")
					.isCloseTo(20.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("with invalid construction arguments")
	final class WithInvalidConstructionArguments
	{
		@Test
		@DisplayName("of() with AVERAGE policy throws IllegalArgumentException")
		void ofWithAveragePolicyThrows()
		{
			assertThatIllegalArgumentException()
					.as("of() with AVERAGE policy must throw IllegalArgumentException")
					.isThrownBy(() -> NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR,
							TieBreakingPolicy.AVERAGE));
		}

		@Test
		@DisplayName("null space throws NullPointerException")
		void nullSpaceThrows()
		{
			assertThatNullPointerException()
					.as("null space must throw NullPointerException")
					.isThrownBy(() -> NearestNeighborMethod.of(null, TieBreakingPolicy.LOWER))
					.withMessage("space");
		}

		@Test
		@DisplayName("null tie-breaking policy throws NullPointerException")
		void nullTieBreakingThrows()
		{
			assertThatNullPointerException()
					.as("null tie-breaking policy must throw NullPointerException")
					.isThrownBy(() -> NearestNeighborMethod.of(DoubleInterpolatableSpace.LINEAR, null))
					.withMessage("tieBreaking");
		}
	}

	@Nested
	@DisplayName("when data is empty")
	final class WhenDataIsEmpty
	{
		@Test
		@DisplayName("fit() throws IllegalArgumentException")
		void fitThrowsOnEmptyData()
		{
			InterpolationData<Double, Double> empty = InterpolationData.of(List.of(), Double::compare);

			assertThatIllegalArgumentException()
					.as("fit() on empty data must throw IllegalArgumentException")
					.isThrownBy(() -> LOWER_METHOD.fit(empty));
		}
	}

	@Nested
	@DisplayName("with null arguments at fit time")
	final class WithNullArgumentsAtFitTime
	{
		@Test
		@DisplayName("null data throws NullPointerException")
		void nullDataThrows()
		{
			assertThatNullPointerException()
					.as("null data must throw NullPointerException")
					.isThrownBy(() -> LOWER_METHOD.fit(null))
					.withMessage("data");
		}
	}
}
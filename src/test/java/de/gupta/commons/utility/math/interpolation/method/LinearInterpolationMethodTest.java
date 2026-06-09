package de.gupta.commons.utility.math.interpolation.method;

import de.gupta.commons.utility.math.analysis.space.standard.DoubleInterpolatableSpace;
import de.gupta.commons.utility.math.analysis.space.standard.DoubleVectorSpaceStructure;
import de.gupta.commons.utility.math.analysis.space.standard.LocalDateInterpolatableSpace;
import de.gupta.commons.utility.math.interpolation.ExtrapolationPolicy;
import de.gupta.commons.utility.math.interpolation.Interpolator;
import de.gupta.commons.utility.math.interpolation.data.InterpolationData;
import de.gupta.commons.utility.math.interpolation.data.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("LinearInterpolationMethod#fit")
final class LinearInterpolationMethodTest
{
	private static final LinearInterpolationMethod<Double, Double, Double> FLAT_METHOD =
			LinearInterpolationMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
					d -> d, ExtrapolationPolicy.FLAT);

	private static InterpolationData<Double, Double> unitRamp()
	{
		return InterpolationData.of(
				List.of(new Sample<>(0.0, 0.0), new Sample<>(10.0, 100.0)),
				Double::compare);
	}

	@Nested
	@DisplayName("when query is interior")
	final class WhenQueryIsInterior
	{
		@Test
		@DisplayName("returns linearly interpolated value")
		void returnsLinearlyInterpolatedValue()
		{
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(unitRamp());

			assertThat(interpolator.interpolate(3.0))
					.as("x=3 must give y=30 on a 0-10 ramp to 0-100")
					.isCloseTo(30.0, within(1e-12));
			assertThat(interpolator.interpolate(7.5))
					.as("x=7.5 must give y=75 on a 0-10 ramp to 0-100")
					.isCloseTo(75.0, within(1e-12));
		}

		@Test
		@DisplayName("returns the exact knot value when query matches a knot")
		void returnsExactKnotValueWhenQueryMatchesKnot()
		{
			InterpolationData<Double, Double> data = InterpolationData.of(
					List.of(new Sample<>(1.0, 10.0), new Sample<>(5.0, 50.0), new Sample<>(9.0, 90.0)),
					Double::compare);
			Interpolator<Double, Double> interpolator = FLAT_METHOD.fit(data);

			assertThat(interpolator.interpolate(1.0))
					.as("query equal to first knot must return exact knot value")
					.isCloseTo(10.0, within(1e-12));
			assertThat(interpolator.interpolate(5.0))
					.as("query equal to interior knot must return exact knot value")
					.isCloseTo(50.0, within(1e-12));
			assertThat(interpolator.interpolate(9.0))
					.as("query equal to last knot must return exact knot value")
					.isCloseTo(90.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when query is out of range — FLAT policy")
	final class WhenQueryIsOutOfRangeWithFlatPolicy
	{
		@Test
		@DisplayName("returns leftmost knot value for query below minimum")
		void returnsLeftmostValueBelowMinimum()
		{
			assertThat(FLAT_METHOD.fit(unitRamp()).interpolate(-99.0))
					.as("FLAT: query below minimum must return leftmost y")
					.isCloseTo(0.0, within(1e-12));
		}

		@Test
		@DisplayName("returns rightmost knot value for query above maximum")
		void returnsRightmostValueAboveMaximum()
		{
			assertThat(FLAT_METHOD.fit(unitRamp()).interpolate(99.0))
					.as("FLAT: query above maximum must return rightmost y")
					.isCloseTo(100.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when query is out of range — LINEAR policy")
	final class WhenQueryIsOutOfRangeWithLinearPolicy
	{
		private final LinearInterpolationMethod<Double, Double, Double> method =
				LinearInterpolationMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
						d -> d, ExtrapolationPolicy.LINEAR);

		@Test
		@DisplayName("extrapolates left segment below minimum")
		void extrapolatesLeftSegmentBelowMinimum()
		{
			assertThat(method.fit(unitRamp()).interpolate(-1.0))
					.as("LINEAR: x=-1 must extrapolate to y=-10 from the left segment")
					.isCloseTo(-10.0, within(1e-12));
		}

		@Test
		@DisplayName("extrapolates right segment above maximum")
		void extrapolatesRightSegmentAboveMaximum()
		{
			assertThat(method.fit(unitRamp()).interpolate(11.0))
					.as("LINEAR: x=11 must extrapolate to y=110 from the right segment")
					.isCloseTo(110.0, within(1e-12));
		}
	}

	@Nested
	@DisplayName("when query is out of range — FORBIDDEN policy")
	final class WhenQueryIsOutOfRangeWithForbiddenPolicy
	{
		private final LinearInterpolationMethod<Double, Double, Double> method =
				LinearInterpolationMethod.of(DoubleInterpolatableSpace.LINEAR, DoubleVectorSpaceStructure.INSTANCE,
						d -> d, ExtrapolationPolicy.FORBIDDEN);

		@Test
		@DisplayName("throws IllegalArgumentException for query below minimum")
		void throwsBelowMinimum()
		{
			assertThatIllegalArgumentException()
					.as("FORBIDDEN: query below minimum must throw")
					.isThrownBy(() -> method.fit(unitRamp()).interpolate(-1.0));
		}

		@Test
		@DisplayName("throws IllegalArgumentException for query above maximum")
		void throwsAboveMaximum()
		{
			assertThatIllegalArgumentException()
					.as("FORBIDDEN: query above maximum must throw")
					.isThrownBy(() -> method.fit(unitRamp()).interpolate(11.0));
		}
	}

	@Nested
	@DisplayName("when X is LocalDate")
	final class WhenXIsLocalDate
	{
		@Test
		@DisplayName("interpolates correctly between two dates")
		void interpolatesCorrectlyBetweenTwoDates()
		{
			InterpolationData<LocalDate, Double> data = InterpolationData.of(
					List.of(
							new Sample<>(LocalDate.of(2024, 1, 1), 0.0),
							new Sample<>(LocalDate.of(2024, 12, 31), 365.0)
					),
					LocalDate::compareTo);

			LinearInterpolationMethod<LocalDate, Double, Double> method =
					LinearInterpolationMethod.of(LocalDateInterpolatableSpace.INSTANCE,
							DoubleVectorSpaceStructure.INSTANCE, d -> d, ExtrapolationPolicy.FLAT);

			assertThat(method.fit(data).interpolate(LocalDate.of(2024, 6, 30)))
					.as("2024-06-30 is 181 days into a 365-day range mapped to [0, 365]")
					.isCloseTo(181.0, within(1e-9));
		}
	}

	@Nested
	@DisplayName("with invalid data")
	final class WithInvalidData
	{
		@Test
		@DisplayName("fit() throws when data has fewer than two samples")
		void fitThrowsWithFewerThanTwoSamples()
		{
			InterpolationData<Double, Double> singleKnot = InterpolationData.of(
					List.of(new Sample<>(1.0, 1.0)), Double::compare);

			assertThatIllegalArgumentException()
					.as("fit() must throw when data has only one sample")
					.isThrownBy(() -> FLAT_METHOD.fit(singleKnot));
		}

		@Test
		@DisplayName("fit() throws when data is empty")
		void fitThrowsWithEmptyData()
		{
			InterpolationData<Double, Double> empty = InterpolationData.of(List.of(), Double::compare);

			assertThatIllegalArgumentException()
					.as("fit() must throw when data is empty")
					.isThrownBy(() -> FLAT_METHOD.fit(empty));
		}

		@Test
		@DisplayName("fit() throws NullPointerException on null data")
		void fitThrowsOnNullData()
		{
			assertThatNullPointerException()
					.as("fit() must throw NullPointerException on null data")
					.isThrownBy(() -> FLAT_METHOD.fit(null))
					.withMessage("data");
		}
	}
}
package de.gupta.commons.utility.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MathUtility Tests")
final class MathUtilityTest
{
	@Nested
	@DisplayName("round Tests")
	final class RoundTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validRoundTestCases")
		@DisplayName("Test round operation with valid inputs")
		void testRoundValidInputs(final double value, final int places, final String description, final double expected)
		{
			double result = MathUtility.round(value, places);

			assertThat(result).as(description)
							  .isCloseTo(expected, within(0.0000001));
		}

		@ParameterizedTest(name = "places = {0}")
		@MethodSource("negativePlacesTestCases")
		@DisplayName("Test round with negative places throws IllegalArgumentException")
		void testRoundNegativePlacesThrowsException(final int places)
		{
			assertThatThrownBy(() -> MathUtility.round(3.14159, places))
					.as("MathUtility.round(3.14159, %d) should throw IllegalArgumentException for negative places",
							places)
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("Test round with zero places")
		void testRoundZeroPlaces()
		{
			double result = MathUtility.round(3.14159, 0);

			assertThat(result).as("MathUtility.round(3.14159, 0) should return 3.0 but was %f", result)
							  .isEqualTo(3.0);
		}

		@Test
		@DisplayName("Test round with large places value")
		void testRoundLargePlaces()
		{
			double result = MathUtility.round(3.14159, 10);

			assertThat(result).as(
									  "MathUtility.round(3.14159, 10) should preserve precision and return 3.14159 but was %f", result)
							  .isEqualTo(3.14159);
		}

		@Test
		@DisplayName("Test round with negative value")
		void testRoundNegativeValue()
		{
			double result = MathUtility.round(-3.14159, 2);

			assertThat(result).as("MathUtility.round(-3.14159, 2) should return -3.14 but was %f", result)
							  .isCloseTo(-3.14, within(0.0000001));
		}

		private static Stream<Arguments> validRoundTestCases()
		{
			return Stream.of(
					RoundTestCase.of(3.14159, 2, "Round pi to 2 decimal places", 3.14),
					RoundTestCase.of(3.14159, 3, "Round pi to 3 decimal places", 3.142),
					RoundTestCase.of(3.14159, 4, "Round pi to 4 decimal places", 3.1416),
					RoundTestCase.of(2.5, 0, "Round 2.5 to nearest integer", 3.0),
					RoundTestCase.of(1.5, 0, "Round 1.5 to nearest integer", 2.0),
					RoundTestCase.of(0.0, 2, "Round zero", 0.0),
					RoundTestCase.of(123.456789, 1, "Round large number to 1 decimal", 123.5)
			).map(testCase -> Arguments.of(testCase.value(), testCase.places(), testCase.description(),
					testCase.expected()));
		}

		private static Stream<Arguments> negativePlacesTestCases()
		{
			return Stream.of(
					Arguments.of(-1),
					Arguments.of(-5),
					Arguments.of(-100),
					Arguments.of(Integer.MIN_VALUE)
			);
		}

		private record RoundTestCase(double value, int places, String description, double expected)
		{
			private static RoundTestCase of(final double value, final int places, final String description,
											final double expected)
			{
				return new RoundTestCase(value, places, description, expected);
			}
		}
	}

	@Nested
	@DisplayName("relativeDeviationFromPerfectValueWithinBounds Tests")
	final class RelativeDeviationTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("validRelativeDeviationTestCases")
		@DisplayName("Test relative deviation calculation with valid inputs")
		void testRelativeDeviationValidInputs(final double value, final double perfectValue, final double floorGrace,
											  final double ceilingGrace, final String description,
											  final double expected)
		{
			double result = MathUtility.relativeDeviationFromPerfectValueWithinBounds(value, perfectValue, floorGrace,
					ceilingGrace);

			assertThat(result).as(description)
							  .isCloseTo(expected, within(0.0000001));
		}

		@Test
		@DisplayName("Test relative deviation when value equals perfect value")
		void testRelativeDeviationValueEqualsPerfect()
		{
			double result = MathUtility.relativeDeviationFromPerfectValueWithinBounds(10.0, 10.0, 8.0, 12.0);

			assertThat(result).as(
									  "relativeDeviationFromPerfectValueWithinBounds(10.0, 10.0, 8.0, 12.0) should return 1.0 when value equals perfect value but was %f",
									  result)
							  .isEqualTo(1.0);
		}

		@Test
		@DisplayName("Test relative deviation when value equals floor grace")
		void testRelativeDeviationValueEqualsFloor()
		{
			double result = MathUtility.relativeDeviationFromPerfectValueWithinBounds(8.0, 10.0, 8.0, 12.0);

			assertThat(result).as(
									  "relativeDeviationFromPerfectValueWithinBounds(8.0, 10.0, 8.0, 12.0) should return 1.0 when value equals floor grace but was %f",
									  result)
							  .isEqualTo(1.0);
		}

		@Test
		@DisplayName("Test relative deviation when value equals ceiling grace")
		void testRelativeDeviationValueEqualsCeiling()
		{
			double result = MathUtility.relativeDeviationFromPerfectValueWithinBounds(12.0, 10.0, 8.0, 12.0);

			assertThat(result).as(
									  "relativeDeviationFromPerfectValueWithinBounds(12.0, 10.0, 8.0, 12.0) should return 1.0 when value equals ceiling grace but was %f",
									  result)
							  .isEqualTo(1.0);
		}

		private static Stream<Arguments> validRelativeDeviationTestCases()
		{
			return Stream.of(
					RelativeDeviationTestCase.of(5.0, 10.0, 8.0, 12.0, "Value below floor grace", -0.6),
					RelativeDeviationTestCase.of(15.0, 10.0, 8.0, 12.0, "Value above ceiling grace", -0.6),
					RelativeDeviationTestCase.of(9.0, 10.0, 8.0, 12.0, "Value between floor and perfect", 0.5),
					RelativeDeviationTestCase.of(11.0, 10.0, 8.0, 12.0, "Value between perfect and ceiling", 0.5)
			).map(testCase -> Arguments.of(testCase.value(), testCase.perfectValue(), testCase.floorGrace(),
					testCase.ceilingGrace(), testCase.description(), testCase.expected()));
		}

		private record RelativeDeviationTestCase(double value, double perfectValue, double floorGrace,
												 double ceilingGrace, String description, double expected)
		{
			private static RelativeDeviationTestCase of(final double value, final double perfectValue,
														final double floorGrace, final double ceilingGrace,
														final String description, final double expected)
			{
				return new RelativeDeviationTestCase(value, perfectValue, floorGrace, ceilingGrace, description,
						expected);
			}
		}
	}

	@Nested
	@DisplayName("gcd Tests")
	final class GcdTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validGcdTestCases")
		@DisplayName("Test gcd calculation with valid inputs")
		void testGcdValidInputs(final int a, final int b, final String description, final int expected)
		{
			int result = MathUtility.gcd(a, b);

			assertThat(result).as(description)
							  .isEqualTo(expected);
		}

		@Test
		@DisplayName("Test gcd with zero as first parameter")
		void testGcdZeroFirst()
		{
			int result = MathUtility.gcd(0, 15);

			assertThat(result).as(
									  "MathUtility.gcd(0, 15) should return 15 (absolute value of non-zero parameter) but was %d", result)
							  .isEqualTo(15);
		}

		@Test
		@DisplayName("Test gcd with zero as second parameter")
		void testGcdZeroSecond()
		{
			int result = MathUtility.gcd(15, 0);

			assertThat(result).as(
									  "MathUtility.gcd(15, 0) should return 15 (absolute value of non-zero parameter) but was %d", result)
							  .isEqualTo(15);
		}

		@Test
		@DisplayName("Test gcd with both parameters zero")
		void testGcdBothZero()
		{
			int result = MathUtility.gcd(0, 0);

			assertThat(result).as("MathUtility.gcd(0, 0) should return 0 but was %d", result)
							  .isEqualTo(0);
		}

		@Test
		@DisplayName("Test gcd with negative numbers")
		void testGcdNegativeNumbers()
		{
			int result = MathUtility.gcd(-12, -8);

			assertThat(result).as("MathUtility.gcd(-12, -8) should return 4 (positive GCD) but was %d", result)
							  .isEqualTo(4);
		}

		private static Stream<Arguments> validGcdTestCases()
		{
			return Stream.of(
					GcdTestCase.of(12, 8, "GCD of 12 and 8", 4),
					GcdTestCase.of(48, 18, "GCD of 48 and 18", 6),
					GcdTestCase.of(17, 13, "GCD of coprime numbers", 1),
					GcdTestCase.of(100, 25, "GCD where one divides the other", 25),
					GcdTestCase.of(1, 1, "GCD of 1 and 1", 1),
					GcdTestCase.of(-15, 10, "GCD with one negative number", 5),
					GcdTestCase.of(7, -21, "GCD with other negative number", 7)
			).map(testCase -> Arguments.of(testCase.a(), testCase.b(), testCase.description(), testCase.expected()));
		}

		private record GcdTestCase(int a, int b, String description, int expected)
		{
			private static GcdTestCase of(final int a, final int b, final String description, final int expected)
			{
				return new GcdTestCase(a, b, description, expected);
			}
		}
	}

	@Nested
	@DisplayName("positiveDivisors Tests")
	final class PositiveDivisorsTests
	{
		@ParameterizedTest(name = "{1}")
		@MethodSource("validPositiveDivisorsTestCases")
		@DisplayName("Test positive divisors calculation with valid inputs")
		void testPositiveDivisorsValidInputs(final int num, final String description, final Set<Integer> expected)
		{
			Set<Integer> result = MathUtility.positiveDivisors(num);

			assertThat(result).as(description)
							  .isNotNull()
							  .containsExactlyInAnyOrderElementsOf(expected);
		}

		@Test
		@DisplayName("Test positive divisors of zero")
		void testPositiveDivisorsZero()
		{
			Set<Integer> result = MathUtility.positiveDivisors(0);

			assertThat(result).as("MathUtility.positiveDivisors(0) should return empty set but was %s", result)
							  .isEmpty();
		}

		@Test
		@DisplayName("Test positive divisors of negative number")
		void testPositiveDivisorsNegative()
		{
			Set<Integer> result = MathUtility.positiveDivisors(-12);

			assertThat(result).as("MathUtility.positiveDivisors(-12) should return [1, 2, 3, 4, 6, 12] but was %s",
									  result)
							  .containsExactlyInAnyOrder(1, 2, 3, 4, 6, 12);
		}

		@Test
		@DisplayName("Test positive divisors result is not null and contains expected size")
		void testPositiveDivisorsResultProperties()
		{
			Set<Integer> result = MathUtility.positiveDivisors(6);

			assertThat(result).as(
									  "MathUtility.positiveDivisors(6) should return non-null set of size 4 but was %s (size: %d)",
									  result, result != null ? result.size() : -1)
							  .isNotNull()
							  .hasSize(4);
		}

		private static Stream<Arguments> validPositiveDivisorsTestCases()
		{
			return Stream.of(
					PositiveDivisorsTestCase.of(1, "Divisors of 1", Set.of(1)),
					PositiveDivisorsTestCase.of(6, "Divisors of 6", Set.of(1, 2, 3, 6)),
					PositiveDivisorsTestCase.of(12, "Divisors of 12", Set.of(1, 2, 3, 4, 6, 12)),
					PositiveDivisorsTestCase.of(7, "Divisors of prime number", Set.of(1, 7)),
					PositiveDivisorsTestCase.of(16, "Divisors of perfect square", Set.of(1, 2, 4, 8, 16)),
					PositiveDivisorsTestCase.of(100, "Divisors of 100", Set.of(1, 2, 4, 5, 10, 20, 25, 50, 100))
			).map(testCase -> Arguments.of(testCase.num(), testCase.description(), testCase.expected()));
		}

		private record PositiveDivisorsTestCase(int num, String description, Set<Integer> expected)
		{
			private static PositiveDivisorsTestCase of(final int num, final String description,
													   final Set<Integer> expected)
			{
				return new PositiveDivisorsTestCase(num, description, expected);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("Test private constructor")
		void testPrivateConstructor() throws Exception
		{
			Constructor<MathUtility> constructor = MathUtility.class.getDeclaredConstructor();

			assertThat(Modifier.isPrivate(constructor.getModifiers())).as("Constructor should be private")
																	  .isTrue();
		}

		@Test
		@DisplayName("Test class is final")
		void testClassIsFinal()
		{
			assertThat(Modifier.isFinal(MathUtility.class.getModifiers())).as("Class should be final")
																		  .isTrue();
		}
	}
}
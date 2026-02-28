package de.gupta.commons.utility.math;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("FunctionUtility Tests")
class FunctionUtilityTest
{
	@Nested
	@DisplayName("multiplyWithExponents Tests")
	final class MultiplyWithExponentsTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("validMultiplyWithExponentsTestCases")
		@DisplayName("Test multiplyWithExponents with valid inputs")
		void testMultiplyWithExponentsValidInputs(final Map<DoubleUnaryOperator, Integer> functionMap,
												  final double inputValue, final double expected,
												  final String description)
		{
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(functionMap);

			assertThat(result.applyAsDouble(inputValue)).as(description)
														.isCloseTo(expected, within(0.001));
		}

		@Test
		@DisplayName("Test multiplyWithExponents with empty map returns identity function")
		void testMultiplyWithExponentsEmptyMap()
		{
			Map<DoubleUnaryOperator, Integer> emptyMap = Map.of();
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(emptyMap);

			assertThat(result.applyAsDouble(5.0)).as("Empty map should return 1 (multiplicative identity)")
												 .isEqualTo(1.0);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with single function")
		void testMultiplyWithExponentsSingleFunction()
		{
			Map<DoubleUnaryOperator, Integer> singleFunctionMap = Map.of(x -> x * 2, 3);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(singleFunctionMap);

			assertThat(result.applyAsDouble(2.0)).as("Single function (x * 2)^3 with input 2 should be 4^3 = 64")
												 .isEqualTo(64.0);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with zero exponent")
		void testMultiplyWithExponentsZeroExponent()
		{
			Map<DoubleUnaryOperator, Integer> zeroExponentMap = Map.of(x -> x * 10, 0);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(zeroExponentMap);

			assertThat(result.applyAsDouble(5.0)).as("Any function raised to power 0 should equal 1")
												 .isEqualTo(1.0);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with negative input value")
		void testMultiplyWithExponentsNegativeInput()
		{
			Map<DoubleUnaryOperator, Integer> functionMap = Map.of(x -> x + 1, 2);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(functionMap);

			assertThat(result.applyAsDouble(-2.0)).as("Function (x + 1)^2 with input -2 should be (-1)^2 = 1")
												  .isEqualTo(1.0);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with null map")
		void testMultiplyWithExponentsNullMap()
		{
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(null);

			assertThatThrownBy(() -> result.applyAsDouble(5.0))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with negative exponent")
		void testMultiplyWithExponentsNegativeExponent()
		{
			Map<DoubleUnaryOperator, Integer> negativeExponentMap = Map.of(x -> x + 1, -2);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(negativeExponentMap);

			assertThat(result.applyAsDouble(3.0)).as("Function (x + 1)^(-2) with input 3 should be 1/16 = 0.0625")
												 .isCloseTo(0.0625, within(0.001));
		}

		@Test
		@DisplayName("Test multiplyWithExponents with infinite input")
		void testMultiplyWithExponentsInfiniteInput()
		{
			Map<DoubleUnaryOperator, Integer> functionMap = Map.of(x -> x, 1);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(functionMap);

			assertThat(result.applyAsDouble(Double.POSITIVE_INFINITY)).as(
																			  "Function with infinite input should return infinity")
																	  .isEqualTo(Double.POSITIVE_INFINITY);
		}

		@Test
		@DisplayName("Test multiplyWithExponents with NaN input")
		void testMultiplyWithExponentsNaNInput()
		{
			Map<DoubleUnaryOperator, Integer> functionMap = Map.of(x -> x, 1);
			DoubleUnaryOperator result = FunctionUtility.multiplyWithExponents(functionMap);

			assertThat(result.applyAsDouble(Double.NaN)).as("Function with NaN input should return NaN")
														.isNaN();
		}

		private static Stream<Arguments> validMultiplyWithExponentsTestCases()
		{
			return Stream.of(
					MultiplyWithExponentsTestCase.of(
							Map.of(x -> x, 2, x -> x + 1, 1),
							3.0,
							36.0,
							"Functions x^2 * (x+1)^1 with input 3 should be 9 * 4 = 36"
					),
					MultiplyWithExponentsTestCase.of(
							Map.of(x -> x * 2, 1, x -> x / 2, 2),
							4.0,
							32.0,
							"Functions (2x)^1 * (x/2)^2 with input 4 should be 8 * 4 = 32"
					),
					MultiplyWithExponentsTestCase.of(
							Map.of(Math::sin, 2),
							Math.PI / 2,
							1.0,
							"Function sin^2(π/2) should equal 1"
					),
					MultiplyWithExponentsTestCase.of(
							Map.of(x -> x, 1, _ -> 2.0, 3),
							5.0,
							40.0,
							"Functions x^1 * 2^3 with input 5 should be 5 * 8 = 40"
					)
			).map(MultiplyWithExponentsTestCase::toArguments);
		}

		private record MultiplyWithExponentsTestCase(Map<DoubleUnaryOperator, Integer> functionMap, double inputValue,
													 double expected, String description)
		{
			static MultiplyWithExponentsTestCase of(final Map<DoubleUnaryOperator, Integer> functionMap,
													final double inputValue, final double expected,
													final String description)
			{
				return new MultiplyWithExponentsTestCase(functionMap, inputValue, expected, description);
			}

			Arguments toArguments()
			{
				return Arguments.of(functionMap, inputValue, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("fromMultiplication Tests")
	final class FromMultiplicationTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("validFromMultiplicationTestCases")
		@DisplayName("Test fromMultiplication with valid inputs")
		void testFromMultiplicationValidInputs(final double multiplier, final double inputValue,
											   final String description, final double expected)
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(multiplier);

			assertThat(result.applyAsDouble(inputValue)).as(description)
														.isCloseTo(expected, within(0.001));
		}

		@Test
		@DisplayName("Test fromMultiplication with zero multiplier")
		void testFromMultiplicationZeroMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(0.0);

			assertThat(result.applyAsDouble(42.0)).as("Multiplication by zero should always return zero")
												  .isEqualTo(0.0);
		}

		@Test
		@DisplayName("Test fromMultiplication with identity multiplier")
		void testFromMultiplicationIdentityMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(1.0);

			assertThat(result.applyAsDouble(7.5)).as("Multiplication by one should return input unchanged")
												 .isEqualTo(7.5);
		}

		@Test
		@DisplayName("Test fromMultiplication with negative multiplier")
		void testFromMultiplicationNegativeMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(-2.0);

			assertThat(result.applyAsDouble(3.0)).as("Multiplication by -2 should negate and double the input")
												 .isEqualTo(-6.0);
		}

		@Test
		@DisplayName("Test fromMultiplication with infinite multiplier")
		void testFromMultiplicationInfiniteMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(Double.POSITIVE_INFINITY);

			assertThat(result.applyAsDouble(1.0)).as(
														 "Multiplication by positive infinity should return positive infinity")
												 .isEqualTo(Double.POSITIVE_INFINITY);
		}

		@Test
		@DisplayName("Test fromMultiplication with NaN multiplier")
		void testFromMultiplicationNaNMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(Double.NaN);

			assertThat(result.applyAsDouble(1.0)).as("Multiplication by NaN should return NaN")
												 .isNaN();
		}

		@Test
		@DisplayName("Test fromMultiplication with very small multiplier")
		void testFromMultiplicationVerySmallMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(Double.MIN_VALUE);

			assertThat(result.applyAsDouble(1.0)).as("Multiplication by MIN_VALUE should return MIN_VALUE")
												 .isEqualTo(Double.MIN_VALUE);
		}

		@Test
		@DisplayName("Test fromMultiplication with very large input")
		void testFromMultiplicationVeryLargeInput()
		{
			DoubleUnaryOperator result = FunctionUtility.fromMultiplication(2.0);

			assertThat(result.applyAsDouble(Double.MAX_VALUE)).as(
																	  "Multiplication of MAX_VALUE by 2 should return positive infinity")
															  .isEqualTo(Double.POSITIVE_INFINITY);
		}

		private static Stream<Arguments> validFromMultiplicationTestCases()
		{
			return Stream.of(
					FromMultiplicationTestCase.of(2.0, 3.0, "Multiplying 3 by 2 should equal 6", 6.0),
					FromMultiplicationTestCase.of(0.5, 10.0, "Multiplying 10 by 0.5 should equal 5", 5.0),
					FromMultiplicationTestCase.of(-3.0, 4.0, "Multiplying 4 by -3 should equal -12", -12.0),
					FromMultiplicationTestCase.of(1.5, -2.0, "Multiplying -2 by 1.5 should equal -3", -3.0),
					FromMultiplicationTestCase.of(0.1, 100.0, "Multiplying 100 by 0.1 should equal 10", 10.0),
					FromMultiplicationTestCase.of(Math.PI, 2.0, "Multiplying 2 by π should equal 2π", 2.0 * Math.PI)
			).map(FromMultiplicationTestCase::toArguments);
		}

		private record FromMultiplicationTestCase(double multiplier, double inputValue, String description,
												  double expected)
		{
			static FromMultiplicationTestCase of(final double multiplier, final double inputValue,
												 final String description, final double expected)
			{
				return new FromMultiplicationTestCase(multiplier, inputValue, description, expected);
			}

			Arguments toArguments()
			{
				return Arguments.of(multiplier, inputValue, description, expected);
			}
		}
	}

	@Nested
	@DisplayName("fromAdditionAndMultiplication Tests")
	final class FromAdditionAndMultiplicationTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("validFromAdditionAndMultiplicationTestCases")
		@DisplayName("Test fromAdditionAndMultiplication with valid inputs")
		void testFromAdditionAndMultiplicationValidInputs(final double summand, final double multiplier,
														  final double inputValue, final double expected,
														  final String description)
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(summand, multiplier);

			assertThat(result.applyAsDouble(inputValue)).as(description)
														.isCloseTo(expected, within(0.001));
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with zero summand")
		void testFromAdditionAndMultiplicationZeroSummand()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(0.0, 3.0);

			assertThat(result.applyAsDouble(4.0)).as("Zero summand should only multiply: (4 + 0) * 3 = 12")
												 .isEqualTo(12.0);
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with zero multiplier")
		void testFromAdditionAndMultiplicationZeroMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(5.0, 0.0);

			assertThat(result.applyAsDouble(7.0)).as("Zero multiplier should always return zero: (7 + 5) * 0 = 0")
												 .isEqualTo(0.0);
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with identity values")
		void testFromAdditionAndMultiplicationIdentityValues()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(0.0, 1.0);

			assertThat(result.applyAsDouble(8.5)).as(
														 "Zero summand and unit multiplier should return input: (8.5 + 0) * 1 = 8.5")
												 .isEqualTo(8.5);
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with negative values")
		void testFromAdditionAndMultiplicationNegativeValues()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(-2.0, -3.0);

			assertThat(result.applyAsDouble(1.0)).as(
														 "Negative summand and multiplier: (1 + (-2)) * (-3) = (-1) * (-3) = 3")
												 .isEqualTo(3.0);
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication order of operations")
		void testFromAdditionAndMultiplicationOrderOfOperations()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(2.0, 3.0);

			assertThat(result.applyAsDouble(4.0)).as("Addition before multiplication: (4 + 2) * 3 = 6 * 3 = 18")
												 .isEqualTo(18.0);
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with NaN summand")
		void testFromAdditionAndMultiplicationNaNSummand()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(Double.NaN, 2.0);

			assertThat(result.applyAsDouble(5.0)).as("Addition with NaN summand should return NaN")
												 .isNaN();
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with NaN multiplier")
		void testFromAdditionAndMultiplicationNaNMultiplier()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(1.0, Double.NaN);

			assertThat(result.applyAsDouble(5.0)).as("Multiplication by NaN should return NaN")
												 .isNaN();
		}

		@Test
		@DisplayName("Test fromAdditionAndMultiplication with overflow scenario")
		void testFromAdditionAndMultiplicationOverflow()
		{
			DoubleUnaryOperator result = FunctionUtility.fromAdditionAndMultiplication(0.0, Double.MAX_VALUE);

			assertThat(result.applyAsDouble(2.0)).as("Multiplying 2 by MAX_VALUE should return positive infinity")
												 .isEqualTo(Double.POSITIVE_INFINITY);
		}

		private static Stream<Arguments> validFromAdditionAndMultiplicationTestCases()
		{
			return Stream.of(
					FromAdditionAndMultiplicationTestCase.of(1.0, 2.0, 3.0, 8.0,
							"Function (x + 1) * 2 with input 3 should equal 8"),
					FromAdditionAndMultiplicationTestCase.of(-1.0, 0.5, 6.0, 2.5,
							"Function (x - 1) * 0.5 with input 6 should equal 2.5"),
					FromAdditionAndMultiplicationTestCase.of(5.0, -2.0, 0.0, -10.0,
							"Function (x + 5) * (-2) with input 0 should equal -10"),
					FromAdditionAndMultiplicationTestCase.of(0.5, 4.0, 2.5, 12.0,
							"Function (x + 0.5) * 4 with input 2.5 should equal 12"),
					FromAdditionAndMultiplicationTestCase.of(-3.0, 1.5, -1.0, -6.0,
							"Function (x - 3) * 1.5 with input -1 should equal -6"),
					FromAdditionAndMultiplicationTestCase.of(Math.E, Math.PI, 0.0, Math.E * Math.PI,
							"Function (x + e) * π with input 0 should equal e * π")
			).map(FromAdditionAndMultiplicationTestCase::toArguments);
		}

		private record FromAdditionAndMultiplicationTestCase(double summand, double multiplier, double inputValue,
															 double expected, String description)
		{
			static FromAdditionAndMultiplicationTestCase of(final double summand, final double multiplier,
															final double inputValue, final double expected,
															final String description)
			{
				return new FromAdditionAndMultiplicationTestCase(summand, multiplier, inputValue, expected,
						description);
			}

			Arguments toArguments()
			{
				return Arguments.of(summand, multiplier, inputValue, expected, description);
			}
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("Test FunctionUtility constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> FunctionUtility.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test FunctionUtility class is final")
		void testClassIsFinal()
		{
			assertThat(FunctionUtility.class.getModifiers())
					.as("FunctionUtility class should be final")
					.satisfies(modifiers -> assertThat(Modifier.isFinal(modifiers)).isTrue());
		}
	}
}
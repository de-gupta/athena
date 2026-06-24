package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("IntegersAsEuclideanDomain")
final class IntegersAsEuclideanDomainTest
{
	@Nested
	@DisplayName("when adding")
	final class WhenAdding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addsTwoValuesCorrectlyCases")
		@DisplayName("adds two values correctly")
		void addsTwoValuesCorrectly(final String as, final long left, final long right, final long expected)
		{
			assertThat(IntegralNumberFactory.of(left).add(IntegralNumberFactory.of(right)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsOnOverflowCases")
		@DisplayName("throws ArithmeticException on overflow")
		void throwsOnOverflow(final String as, final long left, final long right)
		{
			assertThatThrownBy(() -> IntegralNumberFactory.of(left).add(IntegralNumberFactory.of(right)))
					.as(as)
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> addsTwoValuesCorrectlyCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", 3, 4, 7),
					Arguments.of("-3 + 4 = 1", -3, 4, 1),
					Arguments.of("0 + 5 = 5", 0, 5, 5),
					Arguments.of("MAX_VALUE + 0 = MAX", Long.MAX_VALUE, 0, Long.MAX_VALUE)
			);
		}

		private static Stream<Arguments> throwsOnOverflowCases()
		{
			return Stream.of(
					Arguments.of("MAX_VALUE + 1 overflows", Long.MAX_VALUE, 1),
					Arguments.of("MIN_VALUE - 1 underflows", Long.MIN_VALUE, -1)
			);
		}
	}

	@Nested
	@DisplayName("when multiplying")
	final class WhenMultiplying
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("multipliesTwoValuesCorrectlyCases")
		@DisplayName("multiplies two values correctly")
		void multipliesTwoValuesCorrectly(final String as, final long left, final long right, final long expected)
		{
			assertThat(IntegralNumberFactory.of(left).multiply(IntegralNumberFactory.of(right)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsOnOverflowCases")
		@DisplayName("throws ArithmeticException on overflow")
		void throwsOnOverflow(final String as, final long left, final long right)
		{
			assertThatThrownBy(() -> IntegralNumberFactory.of(left).multiply(IntegralNumberFactory.of(right)))
					.as(as)
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> multipliesTwoValuesCorrectlyCases()
		{
			return Stream.of(
					Arguments.of("3 * 4 = 12", 3, 4, 12),
					Arguments.of("-3 * 4 = -12", -3, 4, -12),
					Arguments.of("0 * 5 = 0", 0, 5, 0),
					Arguments.of("1 * 7 = 7", 1, 7, 7)
			);
		}

		private static Stream<Arguments> throwsOnOverflowCases()
		{
			return Stream.of(
					Arguments.of("MAX_VALUE * 2 overflows", Long.MAX_VALUE, 2),
					Arguments.of("MIN_VALUE * 2 overflows", Long.MIN_VALUE, 2),
					Arguments.of("MIN_VALUE * -1 overflows", Long.MIN_VALUE, -1)
			);
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("negatesValueCases")
		@DisplayName("negates value")
		void negatesValue(final String as, final long value, final long expected)
		{
			assertThat(IntegralNumberFactory.of(value).negate())
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		@Test
		@DisplayName("throws ArithmeticException for MIN_VALUE")
		void throwsForMinValue()
		{
			assertThatThrownBy(() -> IntegralNumberFactory.of(Long.MIN_VALUE).negate())
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> negatesValueCases()
		{
			return Stream.of(
					Arguments.of("negate 5 = -5", 5, -5),
					Arguments.of("negate -3 = 3", -3, 3),
					Arguments.of("negate 0 = 0", 0, 0),
					Arguments.of("negate MAX_VALUE = MIN+1", Long.MAX_VALUE, Long.MIN_VALUE + 1)
			);
		}
	}

	@Nested
	@DisplayName("when dividing with remainder")
	final class WhenDividingWithRemainder
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesMathematicallyCorrectQuotientAndRemainderCases")
		@DisplayName("produces mathematically correct quotient and remainder")
		void producesMathematicallyCorrectQuotientAndRemainder(
				final String as, final long dividend, final long divisor,
				final long expectedQuotient, final long expectedRemainder)
		{
			DivisionResult<IntegralNumber> result =
					IntegralNumberFactory.of(dividend).divideWithRemainder(
							IntegralNumberFactory.of(divisor));

			assertThat(result.quotient()).as("%s: quotient", as)
			                             .isEqualTo(IntegralNumberFactory.of(expectedQuotient));
			assertThat(result.remainder()).as("%s: remainder", as)
			                              .isEqualTo(IntegralNumberFactory.of(expectedRemainder));
		}

		@Test
		@DisplayName("remainder satisfies dividend = quotient * divisor + remainder")
		void remainderSatisfiesDivisionIdentity()
		{
			long dividend = -17L;
			long divisor = 5L;

			DivisionResult<IntegralNumber> result =
					IntegralNumberFactory.of(dividend).divideWithRemainder(
							IntegralNumberFactory.of(divisor));

			long reconstructed = result.quotient().value() * divisor + result.remainder().value();
			assertThat(reconstructed).as("q * d + r must equal dividend").isEqualTo(dividend);
		}

		@Test
		@DisplayName("throws ArithmeticException for division by zero")
		void throwsForDivisionByZero()
		{
			assertThatThrownBy(
					() -> IntegralNumberFactory.of(7).divideWithRemainder(IntegralNumberFactory.of(0)))
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> producesMathematicallyCorrectQuotientAndRemainderCases()
		{
			return Stream.of(
					Arguments.of("17 ÷ 5 = 3 r 2", 17, 5, 3, 2),
					Arguments.of("-7 ÷ 3 = -3 r 2", -7, 3, -3, 2),
					Arguments.of("7 ÷ -3 = -3 r -2", 7, -3, -3, -2),
					Arguments.of("0 ÷ 5 = 0 r 0", 0, 5, 0, 0),
					Arguments.of("6 ÷ 3 = 2 r 0", 6, 3, 2, 0)
			);
		}
	}

	@Nested
	@DisplayName("when computing norm")
	final class WhenComputingNorm
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsAbsoluteValueCases")
		@DisplayName("returns absolute value")
		void returnsAbsoluteValue(final String as, final long value, final long expected)
		{
			assertThat(IntegralNumberFactory.of(value).norm()).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("throws ArithmeticException for MIN_VALUE")
		void throwsForMinValue()
		{
			assertThatThrownBy(() -> IntegralNumberFactory.of(Long.MIN_VALUE).norm())
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> returnsAbsoluteValueCases()
		{
			return Stream.of(
					Arguments.of("norm(5) = 5", 5, 5),
					Arguments.of("norm(-5) = 5", -5, 5),
					Arguments.of("norm(0) = 0", 0, 0)
			);
		}
	}

	@Nested
	@DisplayName("when computing gcd")
	final class WhenComputingGcd
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("computesGcdCorrectlyCases")
		@DisplayName("computes gcd correctly")
		void computesGcdCorrectly(final String as, final long a, final long b, final long expected)
		{
			assertThat(IntegralNumberFactory.of(a).gcd(IntegralNumberFactory.of(b)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		private static Stream<Arguments> computesGcdCorrectlyCases()
		{
			return Stream.of(
					Arguments.of("gcd(12, 8) = 4", 12, 8, 4),
					Arguments.of("gcd(84, 30) = 6", 84, 30, 6),
					Arguments.of("gcd(7, 1) = 1", 7, 1, 1),
					Arguments.of("gcd(0, 5) = 5", 0, 5, 5)
			);
		}
	}

	@Nested
	@DisplayName("when checking identity elements")
	final class WhenCheckingIdentityElements
	{
		@Test
		@DisplayName("zero is additive identity")
		void zeroIsAdditiveIdentity()
		{
			IntegralNumber a = IntegralNumberFactory.of(42);

			assertThat(a.add(a.zero())).as("a + 0").isEqualTo(a);
			assertThat(a.zero().add(a)).as("0 + a").isEqualTo(a);
		}

		@Test
		@DisplayName("one is multiplicative identity")
		void oneIsMultiplicativeIdentity()
		{
			IntegralNumber a = IntegralNumberFactory.of(42);

			assertThat(a.multiply(a.one())).as("a * 1").isEqualTo(a);
			assertThat(a.one().multiply(a)).as("1 * a").isEqualTo(a);
		}

		@Test
		@DisplayName("isZero is true only for zero element")
		void isZeroIsTrueOnlyForZeroElement()
		{
			assertThat(IntegralNumberFactory.of(0).isZero()).as("0 is zero").isEqualTo(true);
			assertThat(IntegralNumberFactory.of(1).isZero()).as("1 is not zero").isEqualTo(false);
			assertThat(IntegralNumberFactory.of(-1).isZero()).as("-1 is not zero").isEqualTo(false);
		}
	}
}
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

@DisplayName("IntegralNumber")
final class LongEuclideanDomainStructureTest
{
	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	@Nested
	@DisplayName("when exposing identity elements")
	final class WhenExposingIdentityElements
	{
		@Test
		@DisplayName("zero returns the additive identity")
		void zeroReturnsAdditiveIdentity()
		{
			assertThat(i(42).zero().value()).as("zero").isEqualTo(0L);
		}

		@Test
		@DisplayName("one returns the multiplicative identity")
		void oneReturnsMultiplicativeIdentity()
		{
			assertThat(i(42).one().value()).as("one").isEqualTo(1L);
		}
	}

	@Nested
	@DisplayName("when performing arithmetic")
	final class WhenPerformingArithmetic
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addsCases")
		@DisplayName("adds correctly")
		void addsCorrectly(final String as, final long left, final long right, final long expected)
		{
			assertThat(i(left).add(i(right))).as(as).isEqualTo(i(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("multipliesCases")
		@DisplayName("multiplies correctly")
		void multipliesCorrectly(final String as, final long left, final long right, final long expected)
		{
			assertThat(i(left).multiply(i(right))).as(as).isEqualTo(i(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("negatesCases")
		@DisplayName("negates correctly")
		void negatesCorrectly(final String as, final long value, final long expected)
		{
			assertThat(i(value).negate()).as(as).isEqualTo(i(expected));
		}

		private static Stream<Arguments> addsCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", 3L, 4L, 7L),
					Arguments.of("-3 + 4 = 1", -3L, 4L, 1L),
					Arguments.of("0 + 0 = 0", 0L, 0L, 0L)
			);
		}

		private static Stream<Arguments> multipliesCases()
		{
			return Stream.of(
					Arguments.of("3 * 4 = 12", 3L, 4L, 12L),
					Arguments.of("-3 * 4 = -12", -3L, 4L, -12L),
					Arguments.of("0 * 5 = 0", 0L, 5L, 0L)
			);
		}

		private static Stream<Arguments> negatesCases()
		{
			return Stream.of(
					Arguments.of("negate 5 = -5", 5L, -5L),
					Arguments.of("negate -3 = 3", -3L, 3L),
					Arguments.of("negate 0 = 0", 0L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when checking zero and norm")
	final class WhenCheckingZeroAndNorm
	{
		@Test
		@DisplayName("isZero identifies the zero element")
		void isZeroIdentifiesZeroElement()
		{
			assertThat(i(0).isZero()).as("zero").isTrue();
			assertThat(i(5).isZero()).as("non-zero").isFalse();
			assertThat(i(-1).isZero()).as("negative").isFalse();
		}

		@Test
		@DisplayName("norm returns absolute value")
		void normReturnsAbsoluteValue()
		{
			assertThat(i(-9).norm()).as("negative").isEqualTo(9L);
			assertThat(i(7).norm()).as("positive").isEqualTo(7L);
			assertThat(i(0).norm()).as("zero").isEqualTo(0L);
		}

		@Test
		@DisplayName("throws ArithmeticException for norm of MIN_VALUE")
		void throwsForNormOfMinValue()
		{
			assertThatThrownBy(() -> i(Long.MIN_VALUE).norm())
					.isInstanceOf(ArithmeticException.class);
		}
	}

	@Nested
	@DisplayName("when dividing with floor semantics")
	final class WhenDividingWithFloorSemantics
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("divideFloorCases")
		@DisplayName("divideFloor returns correct quotient and remainder")
		void divideFloorReturnsCorrectQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                                   final long expectedQ, final long expectedR)
		{
			final DivisionResult<IntegralNumber> result = i(dividend).divideFloor(i(divisor));
			assertThat(result.quotient()).as("%s: quotient", as).isEqualTo(i(expectedQ));
			assertThat(result.remainder()).as("%s: remainder", as).isEqualTo(i(expectedR));
		}

		private static Stream<Arguments> divideFloorCases()
		{
			return Stream.of(
					Arguments.of("-17 / 5", -17L, 5L, -4L, 3L),
					Arguments.of("17 / 5", 17L, 5L, 3L, 2L),
					Arguments.of("-17 / -5", -17L, -5L, 3L, -2L),
					Arguments.of("10 / 2 (exact)", 10L, 2L, 5L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when arithmetic overflows")
	final class WhenArithmeticOverflows
	{
		@Test
		@DisplayName("add throws on overflow")
		void addThrowsOnOverflow()
		{
			assertThatThrownBy(() -> i(Long.MAX_VALUE).add(i(1L))).isInstanceOf(ArithmeticException.class);
		}

		@Test
		@DisplayName("multiply throws on overflow")
		void multiplyThrowsOnOverflow()
		{
			assertThatThrownBy(() -> i(Long.MAX_VALUE).multiply(i(2L))).isInstanceOf(ArithmeticException.class);
		}

		@Test
		@DisplayName("negate throws on MIN_VALUE overflow")
		void negateThrowsOnMinValueOverflow()
		{
			assertThatThrownBy(() -> i(Long.MIN_VALUE).negate()).isInstanceOf(ArithmeticException.class);
		}
	}
}
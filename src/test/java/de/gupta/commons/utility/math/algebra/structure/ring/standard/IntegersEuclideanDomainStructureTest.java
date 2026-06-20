package de.gupta.commons.utility.math.algebra.structure.ring.standard;

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

@DisplayName("IntegersEuclideanDomainStructure")
final class IntegersEuclideanDomainStructureTest
{
	@Nested
	@DisplayName("when exposing identity elements")
	final class WhenExposingIdentityElements
	{
		@Test
		@DisplayName("returns zero and one")
		void returnsZeroAndOne()
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.zero()).as("zero").isEqualTo(0L);
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.one()).as("one").isEqualTo(1L);
		}
	}

	@Nested
	@DisplayName("when performing arithmetic")
	final class WhenPerformingArithmetic
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addsValuesCases")
		@DisplayName("adds values")
		void addsValues(final String as, final long left, final long right, final long expected)
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.add(left, right)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("multipliesValuesCases")
		@DisplayName("multiplies values")
		void multipliesValues(final String as, final long left, final long right, final long expected)
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.multiply(left, right)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("negatesValuesCases")
		@DisplayName("negates values")
		void negatesValues(final String as, final long value, final long expected)
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.negate(value)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> addsValuesCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", 3L, 4L, 7L),
					Arguments.of("-3 + 4 = 1", -3L, 4L, 1L)
			);
		}

		private static Stream<Arguments> multipliesValuesCases()
		{
			return Stream.of(
					Arguments.of("3 * 4 = 12", 3L, 4L, 12L),
					Arguments.of("-3 * 4 = -12", -3L, 4L, -12L)
			);
		}

		private static Stream<Arguments> negatesValuesCases()
		{
			return Stream.of(
					Arguments.of("negate 5 = -5", 5L, -5L),
					Arguments.of("negate -3 = 3", -3L, 3L)
			);
		}
	}

	@Nested
	@DisplayName("when checking zero and norm")
	final class WhenCheckingZeroAndNorm
	{
		@Test
		@DisplayName("identifies the zero element")
		void identifiesTheZeroElement()
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.isZero(0L)).as("zero element").isEqualTo(true);
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.isZero(5L)).as("non-zero element").isEqualTo(false);
		}

		@Test
		@DisplayName("returns the absolute value as norm")
		void returnsTheAbsoluteValueAsNorm()
		{
			assertThat(IntegersEuclideanDomainStructure.INSTANCE.norm(-9L)).as("norm").isEqualTo(9L);
		}

		@Test
		@DisplayName("throws ArithmeticException for the norm of MIN_VALUE")
		void throwsArithmeticExceptionForTheNormOfMinValue()
		{
			assertThatThrownBy(() -> IntegersEuclideanDomainStructure.INSTANCE.norm(Long.MIN_VALUE))
					.as("norm(Long.MIN_VALUE)")
					.isInstanceOf(ArithmeticException.class);
		}
	}

	@Nested
	@DisplayName("when dividing with remainder")
	final class WhenDividingWithRemainder
	{
		@Test
		@DisplayName("uses floor division semantics")
		void usesFloorDivisionSemantics()
		{
			DivisionResult<Long> result = IntegersEuclideanDomainStructure.INSTANCE.divideWithRemainder(-17L, 5L);

			assertThat(result.quotient()).as("quotient").isEqualTo(-4L);
			assertThat(result.remainder()).as("remainder").isEqualTo(3L);
		}
	}

	@Nested
	@DisplayName("when arithmetic overflows")
	final class WhenArithmeticOverflows
	{
		@Test
		@DisplayName("throws ArithmeticException for overflowing addition")
		void throwsArithmeticExceptionForOverflowingAddition()
		{
			assertThatThrownBy(() -> IntegersEuclideanDomainStructure.INSTANCE.add(Long.MAX_VALUE, 1L))
					.as("MAX_VALUE + 1")
					.isInstanceOf(ArithmeticException.class);
		}

		@Test
		@DisplayName("throws ArithmeticException for overflowing multiplication")
		void throwsArithmeticExceptionForOverflowingMultiplication()
		{
			assertThatThrownBy(() -> IntegersEuclideanDomainStructure.INSTANCE.multiply(Long.MAX_VALUE, 2L))
					.as("MAX_VALUE * 2")
					.isInstanceOf(ArithmeticException.class);
		}

		@Test
		@DisplayName("throws ArithmeticException for overflowing negation")
		void throwsArithmeticExceptionForOverflowingNegation()
		{
			assertThatThrownBy(() -> IntegersEuclideanDomainStructure.INSTANCE.negate(Long.MIN_VALUE))
					.as("negate(MIN_VALUE)")
					.isInstanceOf(ArithmeticException.class);
		}
	}
}
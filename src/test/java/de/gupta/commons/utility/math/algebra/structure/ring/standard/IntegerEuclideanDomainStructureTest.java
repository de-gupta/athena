package de.gupta.commons.utility.math.algebra.structure.ring.standard;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntegerEuclideanDomainStructure")
final class IntegerEuclideanDomainStructureTest
{
	@Nested
	@DisplayName("when exposing identity elements")
	final class WhenExposingIdentityElements
	{
		@Test
		@DisplayName("returns the canonical zero and one elements")
		void returnsTheCanonicalZeroAndOneElements()
		{
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.zero()).as("zero")
			                                                           .isEqualTo(IntegralNumberFactory.of(0));
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.one()).as("one")
			                                                          .isEqualTo(IntegralNumberFactory.of(1));
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
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.add(IntegralNumberFactory.of(left),
					IntegralNumberFactory.of(right)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("multipliesValuesCases")
		@DisplayName("multiplies values")
		void multipliesValues(final String as, final long left, final long right, final long expected)
		{
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.multiply(IntegralNumberFactory.of(left),
					IntegralNumberFactory.of(right)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("negatesValuesCases")
		@DisplayName("negates values")
		void negatesValues(final String as, final long value, final long expected)
		{
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.negate(IntegralNumberFactory.of(value)))
					.as(as)
					.isEqualTo(IntegralNumberFactory.of(expected));
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
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.isZero(IntegralNumberFactory.of(0)))
					.as("zero element")
					.isEqualTo(true);
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.isZero(IntegralNumberFactory.of(5)))
					.as("non-zero element")
					.isEqualTo(false);
		}

		@Test
		@DisplayName("returns the element norm")
		void returnsTheElementNorm()
		{
			assertThat(IntegerEuclideanDomainStructure.INSTANCE.norm(IntegralNumberFactory.of(-9)))
					.as("norm")
					.isEqualTo(9L);
		}
	}

	@Nested
	@DisplayName("when dividing with remainder")
	final class WhenDividingWithRemainder
	{
		@Test
		@DisplayName("delegates to the element implementation")
		void delegatesToTheElementImplementation()
		{
			DivisionResult<IntegralNumbers> result =
					IntegerEuclideanDomainStructure.INSTANCE.divideWithRemainder(
							IntegralNumberFactory.of(-17), IntegralNumberFactory.of(5));

			assertThat(result.quotient()).as("quotient").isEqualTo(IntegralNumberFactory.of(-4));
			assertThat(result.remainder()).as("remainder").isEqualTo(IntegralNumberFactory.of(3));
		}
	}
}
package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RationalNumber")
final class RationalNumberTest
{
	private static RationalNumber r(final long numerator, final long denominator)
	{
		return RationalNumberFactory.of(numerator, denominator);
	}

	@Nested
	@DisplayName("when constructing")
	final class WhenConstructing
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("normalizationCases")
		@DisplayName("normalizes to lowest terms with positive denominator")
		void normalizesToLowestTermsWithPositiveDenominator(final String as,
		                                                    final long num, final long denom,
		                                                    final long expectedNum, final long expectedDenom)
		{
			final RationalNumber r = r(num, denom);
			assertThat(r.numerator().value()).as("%s: numerator", as).isEqualTo(expectedNum);
			assertThat(r.denominator().value()).as("%s: denominator", as).isEqualTo(expectedDenom);
		}

		@Test
		@DisplayName("throws IllegalArgumentException for zero denominator")
		void throwsForZeroDenominator()
		{
			assertThatThrownBy(() -> r(1, 0)).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> r(0, 0)).isInstanceOf(IllegalArgumentException.class);
			assertThatThrownBy(() -> r(-3, 0)).isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("factory zero() and one() return canonical forms")
		void factoryReturnsCanonicalForms()
		{
			assertThat(RationalNumberFactory.zero()).isEqualTo(r(0, 1));
			assertThat(RationalNumberFactory.one()).isEqualTo(r(1, 1));
		}

		private static Stream<Arguments> normalizationCases()
		{
			return Stream.of(
					Arguments.of("1/2 is already reduced", 1, 2, 1L, 2L),
					Arguments.of("2/4 → 1/2", 2, 4, 1L, 2L),
					Arguments.of("6/9 → 2/3", 6, 9, 2L, 3L),
					Arguments.of("5/5 → 1/1", 5, 5, 1L, 1L),
					Arguments.of("12/8 → 3/2", 12, 8, 3L, 2L),
					Arguments.of("0/5 → 0/1", 0, 5, 0L, 1L),
					Arguments.of("0/-3 → 0/1", 0, -3, 0L, 1L),
					Arguments.of("1/-2 → -1/2 (sign moves to numerator)", 1, -2, -1L, 2L),
					Arguments.of("-1/-2 → 1/2 (both negative)", -1, -2, 1L, 2L),
					Arguments.of("6/-9 → -2/3", 6, -9, -2L, 3L),
					Arguments.of("-6/-9 → 2/3", -6, -9, 2L, 3L),
					Arguments.of("-6/9 → -2/3", -6, 9, -2L, 3L)
			);
		}
	}

	@Nested
	@DisplayName("when adding")
	final class WhenAdding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addCases")
		@DisplayName("produces the correct sum")
		void producesTheCorrectSum(final String as, final RationalNumber a, final RationalNumber b,
		                           final RationalNumber expected)
		{
			assertThat(a.add(b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("adding zero is identity")
		void addingZeroIsIdentity()
		{
			final RationalNumber half = r(1, 2);
			assertThat(half.add(half.zero())).isEqualTo(half);
			assertThat(half.zero().add(half)).isEqualTo(half);
		}

		@Test
		@DisplayName("adding inverse yields zero")
		void addingInverseYieldsZero()
		{
			final RationalNumber half = r(1, 2);
			assertThat(half.add(half.negate())).isEqualTo(half.zero());
		}

		private static Stream<Arguments> addCases()
		{
			return Stream.of(
					Arguments.of("1/2 + 1/3 = 5/6", r(1, 2), r(1, 3), r(5, 6)),
					Arguments.of("1/4 + 3/4 = 1", r(1, 4), r(3, 4), r(1, 1)),
					Arguments.of("1/6 + 1/4 = 5/12", r(1, 6), r(1, 4), r(5, 12)),
					Arguments.of("-1/3 + -2/3 = -1", r(-1, 3), r(-2, 3), r(-1, 1)),
					Arguments.of("1/2 + 1/2 = 1", r(1, 2), r(1, 2), r(1, 1)),
					Arguments.of("2/3 + 1/6 = 5/6", r(2, 3), r(1, 6), r(5, 6)),
					Arguments.of("-1/4 + 3/8 = 1/8", r(-1, 4), r(3, 8), r(1, 8))
			);
		}
	}

	@Nested
	@DisplayName("when subtracting")
	final class WhenSubtracting
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("subtractCases")
		@DisplayName("produces the correct difference")
		void producesTheCorrectDifference(final String as, final RationalNumber a, final RationalNumber b,
		                                  final RationalNumber expected)
		{
			assertThat(a.subtract(b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("subtracting self yields zero")
		void subtractingSelfYieldsZero()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.subtract(r)).isEqualTo(r.zero());
		}

		private static Stream<Arguments> subtractCases()
		{
			return Stream.of(
					Arguments.of("3/4 - 1/4 = 1/2", r(3, 4), r(1, 4), r(1, 2)),
					Arguments.of("1/2 - 1/3 = 1/6", r(1, 2), r(1, 3), r(1, 6)),
					Arguments.of("1/3 - 1/2 = -1/6", r(1, 3), r(1, 2), r(-1, 6)),
					Arguments.of("1 - 1/4 = 3/4", r(1, 1), r(1, 4), r(3, 4)),
					Arguments.of("1/2 - (-1/2) = 1", r(1, 2), r(-1, 2), r(1, 1))
			);
		}
	}

	@Nested
	@DisplayName("when multiplying")
	final class WhenMultiplying
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("multiplyCases")
		@DisplayName("produces the correct product")
		void producesTheCorrectProduct(final String as, final RationalNumber a, final RationalNumber b,
		                               final RationalNumber expected)
		{
			assertThat(a.multiply(b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("multiplying by one is identity")
		void multiplyingByOneIsIdentity()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.multiply(r.one())).isEqualTo(r);
			assertThat(r.one().multiply(r)).isEqualTo(r);
		}

		@Test
		@DisplayName("multiplying by zero yields zero")
		void multiplyingByZeroYieldsZero()
		{
			assertThat(r(3, 7).multiply(r(0, 1))).isEqualTo(r(0, 1));
		}

		@Test
		@DisplayName("multiplication is commutative")
		void multiplicationIsCommutative()
		{
			final RationalNumber a = r(2, 3);
			final RationalNumber b = r(5, 7);
			assertThat(a.multiply(b)).isEqualTo(b.multiply(a));
		}

		private static Stream<Arguments> multiplyCases()
		{
			return Stream.of(
					Arguments.of("2/3 * 3/4 = 1/2", r(2, 3), r(3, 4), r(1, 2)),
					Arguments.of("1/2 * 2/1 = 1", r(1, 2), r(2, 1), r(1, 1)),
					Arguments.of("-1/2 * 2/3 = -1/3", r(-1, 2), r(2, 3), r(-1, 3)),
					Arguments.of("(-1/2) * (-2/3) = 1/3", r(-1, 2), r(-2, 3), r(1, 3)),
					Arguments.of("3/4 * 4/3 = 1", r(3, 4), r(4, 3), r(1, 1)),
					Arguments.of("2/5 * 5/8 = 1/4", r(2, 5), r(5, 8), r(1, 4))
			);
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("negateCases")
		@DisplayName("produces the correct negation")
		void producesTheCorrectNegation(final String as, final RationalNumber r, final RationalNumber expected)
		{
			assertThat(r.negate()).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("double negation is identity")
		void doubleNegationIsIdentity()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.negate().negate()).isEqualTo(r);
		}

		private static Stream<Arguments> negateCases()
		{
			return Stream.of(
					Arguments.of("negate 1/2 = -1/2", r(1, 2), r(-1, 2)),
					Arguments.of("negate -3/4 = 3/4", r(-3, 4), r(3, 4)),
					Arguments.of("negate 0 = 0", r(0, 1), r(0, 1)),
					Arguments.of("negate 1 = -1", r(1, 1), r(-1, 1))
			);
		}
	}

	@Nested
	@DisplayName("when computing reciprocal")
	final class WhenComputingReciprocal
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reciprocalCases")
		@DisplayName("returns the multiplicative inverse")
		void returnsTheMultiplicativeInverse(final String as, final RationalNumber r, final RationalNumber expected)
		{
			assertThat(r.reciprocal()).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("reciprocal of reciprocal is identity")
		void reciprocalOfReciprocalIsIdentity()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.reciprocal().reciprocal()).isEqualTo(r);
		}

		@Test
		@DisplayName("r * reciprocal(r) = 1")
		void multiplyByReciprocalYieldsOne()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.multiply(r.reciprocal())).isEqualTo(r.one());
		}

		private static Stream<Arguments> reciprocalCases()
		{
			return Stream.of(
					Arguments.of("reciprocal 2/3 = 3/2", r(2, 3), r(3, 2)),
					Arguments.of("reciprocal 1/2 = 2/1", r(1, 2), r(2, 1)),
					Arguments.of("reciprocal 1/1 = 1/1", r(1, 1), r(1, 1)),
					Arguments.of("reciprocal -3/4 = -4/3", r(-3, 4), r(-4, 3)),
					Arguments.of("reciprocal 3/1 = 1/3", r(3, 1), r(1, 3))
			);
		}
	}

	@Nested
	@DisplayName("when dividing")
	final class WhenDividing
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("divideCases")
		@DisplayName("produces the correct quotient")
		void producesTheCorrectQuotient(final String as, final RationalNumber a, final RationalNumber b,
		                                final RationalNumber expected)
		{
			assertThat(a.divide(b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("dividing by one is identity")
		void dividingByOneIsIdentity()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.divide(r.one())).isEqualTo(r);
		}

		@Test
		@DisplayName("dividing by self yields one")
		void dividingBySelfYieldsOne()
		{
			final RationalNumber r = r(3, 7);
			assertThat(r.divide(r)).isEqualTo(r.one());
		}

		private static Stream<Arguments> divideCases()
		{
			return Stream.of(
					Arguments.of("(1/2) / (3/4) = 2/3", r(1, 2), r(3, 4), r(2, 3)),
					Arguments.of("(2/3) / (2/3) = 1", r(2, 3), r(2, 3), r(1, 1)),
					Arguments.of("(1/2) / 2 = 1/4", r(1, 2), r(2, 1), r(1, 4)),
					Arguments.of("1 / (1/3) = 3", r(1, 1), r(1, 3), r(3, 1)),
					Arguments.of("(-1/2) / (1/4) = -2", r(-1, 2), r(1, 4), r(-2, 1))
			);
		}
	}

	@Nested
	@DisplayName("when accessing identity elements")
	final class WhenAccessingIdentities
	{
		@Test
		@DisplayName("zero has value 0/1")
		void zeroHasValueZeroOverOne()
		{
			final RationalNumber zero = r(1, 2).zero();
			assertThat(zero.numerator().value()).isEqualTo(0L);
			assertThat(zero.denominator().value()).isEqualTo(1L);
		}

		@Test
		@DisplayName("one has value 1/1")
		void oneHasValueOneOverOne()
		{
			final RationalNumber one = r(1, 2).one();
			assertThat(one.numerator().value()).isEqualTo(1L);
			assertThat(one.denominator().value()).isEqualTo(1L);
		}
	}

	@Nested
	@DisplayName("when scaling by additive power")
	final class WhenScalingByAdditivePower
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("powerCases")
		@DisplayName("power(n) = n-fold repeated addition")
		void powerIsNFoldRepeatedAddition(final String as, final RationalNumber r, final int n,
		                                  final RationalNumber expected)
		{
			assertThat(r.power(n)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> powerCases()
		{
			return Stream.of(
					Arguments.of("(1/3) * 3 = 1", r(1, 3), 3, r(1, 1)),
					Arguments.of("(1/4) * 4 = 1", r(1, 4), 4, r(1, 1)),
					Arguments.of("(1/2) * 0 = 0", r(1, 2), 0, r(0, 1)),
					Arguments.of("(1/2) * 1 = 1/2", r(1, 2), 1, r(1, 2)),
					Arguments.of("(1/2) * 2 = 1", r(1, 2), 2, r(1, 1)),
					Arguments.of("(1/2) * (-1) = -1/2 (negated)", r(1, 2), -1, r(-1, 2)),
					Arguments.of("(2/3) * 3 = 2", r(2, 3), 3, r(2, 1))
			);
		}
	}
}
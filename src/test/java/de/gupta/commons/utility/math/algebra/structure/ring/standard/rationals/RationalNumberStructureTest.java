package de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RationalNumberStructure")
final class RationalNumberStructureTest
{
	private static final RationalNumberStructure S = RationalNumberStructureFactory.instance();

	private static RationalNumber r(final long num, final long denom)
	{
		return RationalNumberFactory.of(num, denom);
	}

	@Nested
	@DisplayName("when accessing identities")
	final class WhenAccessingIdentities
	{
		@Test
		@DisplayName("zero returns 0/1")
		void zeroReturnsZeroOverOne()
		{
			assertThat(S.zero()).isEqualTo(r(0, 1));
		}

		@Test
		@DisplayName("one returns 1/1")
		void oneReturnsOneOverOne()
		{
			assertThat(S.one()).isEqualTo(r(1, 1));
		}
	}

	@Nested
	@DisplayName("when adding")
	final class WhenAdding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("addCases")
		@DisplayName("produces correct sum")
		void producesCorrectSum(final String as, final RationalNumber a, final RationalNumber b,
		                        final RationalNumber expected)
		{
			assertThat(S.add(a, b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("adding zero is identity")
		void addingZeroIsIdentity()
		{
			assertThat(S.add(r(2, 3), S.zero())).isEqualTo(r(2, 3));
		}

		private static Stream<Arguments> addCases()
		{
			return Stream.of(
					Arguments.of("1/2 + 1/3 = 5/6", r(1, 2), r(1, 3), r(5, 6)),
					Arguments.of("1/4 + 3/4 = 1", r(1, 4), r(3, 4), r(1, 1)),
					Arguments.of("-1/3 + -2/3 = -1", r(-1, 3), r(-2, 3), r(-1, 1))
			);
		}
	}

	@Nested
	@DisplayName("when subtracting")
	final class WhenSubtracting
	{
		@Test
		@DisplayName("uses default subtract via negate+add")
		void usesDefaultSubtract()
		{
			assertThat(S.subtract(r(3, 4), r(1, 4))).isEqualTo(r(1, 2));
			assertThat(S.subtract(r(1, 2), r(1, 3))).isEqualTo(r(1, 6));
			assertThat(S.subtract(r(1, 1), r(1, 1))).isEqualTo(r(0, 1));
		}
	}

	@Nested
	@DisplayName("when multiplying")
	final class WhenMultiplying
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("multiplyCases")
		@DisplayName("produces correct product")
		void producesCorrectProduct(final String as, final RationalNumber a, final RationalNumber b,
		                            final RationalNumber expected)
		{
			assertThat(S.multiply(a, b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("multiplying by one is identity")
		void multiplyingByOneIsIdentity()
		{
			assertThat(S.multiply(r(3, 7), S.one())).isEqualTo(r(3, 7));
		}

		private static Stream<Arguments> multiplyCases()
		{
			return Stream.of(
					Arguments.of("2/3 * 3/4 = 1/2", r(2, 3), r(3, 4), r(1, 2)),
					Arguments.of("-1/2 * 2/3 = -1/3", r(-1, 2), r(2, 3), r(-1, 3)),
					Arguments.of("3/4 * 0 = 0", r(3, 4), r(0, 1), r(0, 1))
			);
		}
	}

	@Nested
	@DisplayName("when dividing")
	final class WhenDividing
	{
		@Test
		@DisplayName("uses default divide via multiplicativeInverse")
		void usesDefaultDivide()
		{
			assertThat(S.divide(r(1, 2), r(3, 4))).isEqualTo(r(2, 3));
			assertThat(S.divide(r(2, 3), r(2, 3))).isEqualTo(r(1, 1));
			assertThat(S.divide(r(3, 4), S.one())).isEqualTo(r(3, 4));
		}
	}

	@Nested
	@DisplayName("when negating")
	final class WhenNegating
	{
		@Test
		@DisplayName("negates correctly")
		void negatesCorrectly()
		{
			assertThat(S.negate(r(1, 2))).isEqualTo(r(-1, 2));
			assertThat(S.negate(r(-3, 4))).isEqualTo(r(3, 4));
			assertThat(S.negate(r(0, 1))).isEqualTo(r(0, 1));
		}
	}

	@Nested
	@DisplayName("when computing multiplicative inverse")
	final class WhenComputingMultiplicativeInverse
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("inverseCases")
		@DisplayName("returns multiplicative inverse")
		void returnsMultiplicativeInverse(final String as, final RationalNumber r, final RationalNumber expected)
		{
			assertThat(S.multiplicativeInverse(r)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("element times inverse equals one")
		void elementTimesInverseEqualsOne()
		{
			final RationalNumber r = r(3, 7);
			assertThat(S.multiply(r, S.multiplicativeInverse(r))).isEqualTo(S.one());
		}

		private static Stream<Arguments> inverseCases()
		{
			return Stream.of(
					Arguments.of("inverse of 2/3 = 3/2", r(2, 3), r(3, 2)),
					Arguments.of("inverse of 1/2 = 2/1", r(1, 2), r(2, 1)),
					Arguments.of("inverse of -3/4 = -4/3", r(-3, 4), r(-4, 3))
			);
		}
	}

	@Nested
	@DisplayName("when comparing")
	final class WhenComparing
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("compareCases")
		@DisplayName("returns correct order relation")
		void returnsCorrectOrderRelation(final String as, final RationalNumber a, final RationalNumber b,
		                                 final OrderRelation expected)
		{
			assertThat(S.compare(a, b)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> compareCases()
		{
			return Stream.of(
					Arguments.of("1/3 < 1/2", r(1, 3), r(1, 2), OrderRelation.LESS_THAN),
					Arguments.of("1/2 > 1/3", r(1, 2), r(1, 3), OrderRelation.GREATER_THAN),
					Arguments.of("2/4 = 1/2", r(2, 4), r(1, 2), OrderRelation.EQUAL),
					Arguments.of("-1/2 < 1/2", r(-1, 2), r(1, 2), OrderRelation.LESS_THAN)
			);
		}
	}

	@Nested
	@DisplayName("when computing absolute value")
	final class WhenComputingAbsoluteValue
	{
		@Test
		@DisplayName("abs returns non-negative value")
		void absReturnsNonNegativeValue()
		{
			assertThat(S.abs(r(3, 4))).isEqualTo(r(3, 4));
			assertThat(S.abs(r(-3, 4))).isEqualTo(r(3, 4));
			assertThat(S.abs(r(0, 1))).isEqualTo(r(0, 1));
		}
	}

	@Nested
	@DisplayName("when using affine operations")
	final class WhenUsingAffineOperations
	{
		@Test
		@DisplayName("between returns signed displacement")
		void betweenReturnsSignedDisplacement()
		{
			assertThat(S.displacement(r(1, 4), r(3, 4))).isEqualTo(r(1, 2));
			assertThat(S.displacement(r(3, 4), r(1, 4))).isEqualTo(r(-1, 2));
			assertThat(S.displacement(r(1, 3), r(1, 3))).isEqualTo(r(0, 1));
		}

		@Test
		@DisplayName("translate shifts by displacement")
		void translateShiftsByDisplacement()
		{
			assertThat(S.translate(r(1, 4), r(1, 2))).isEqualTo(r(3, 4));
			assertThat(S.translate(r(1, 2), r(-1, 4))).isEqualTo(r(1, 4));
		}

		@Test
		@DisplayName("translate by between round-trips")
		void translateByBetweenRoundTrips()
		{
			final RationalNumber from = r(1, 6);
			final RationalNumber to = r(5, 6);
			assertThat(S.translate(from, S.displacement(from, to))).isEqualTo(to);
		}
	}
}
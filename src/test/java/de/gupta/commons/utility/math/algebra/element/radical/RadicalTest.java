package de.gupta.commons.utility.math.algebra.element.radical;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategies;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
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

@DisplayName("Radical")
final class RadicalTest
{
	private static final ApproximationStrategy<IntegralNumber> BY_EQUALITY =
			ApproximationStrategies.byEquality();
	private static final ApproximationStrategy<RationalNumber> WITHIN_THOUSANDTH =
			ApproximationStrategies.withinTolerance(r(1, 1000));
	private static final ApproximationStrategy<RationalNumber> WITHIN_MILLIONTH =
			ApproximationStrategies.withinTolerance(r(1, 1_000_000));
	// RationalNumber is a field — division is always exact, rounding strategy is irrelevant
	private static final RoundingStrategy<RationalNumber> RATIONAL_ROUNDING =
			(dividend, divisor) -> DivisionResult.of(dividend.divide(divisor), RationalNumberFactory.zero());

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	private static RationalNumber r(final long n, final long d)
	{
		return RationalNumberFactory.of(n, d);
	}

	private static double toDouble(final RationalNumber r)
	{
		return (double) r.numerator().value() / r.denominator().value();
	}

	private static void assertWithinTolerance(final String as, final RationalNumber actual,
	                                          final RationalNumber expected, final RationalNumber tolerance)
	{
		assertThat(actual.subtract(expected).abs().compare(tolerance).isLessThanOrEqualTo())
				.as(as)
				.isTrue();
	}

	@Nested
	@DisplayName("when computing integer square roots")
	final class WhenComputingIntegerSquareRoots
	{
		@Test
		@DisplayName("zero returns zero")
		void zeroReturnsZero()
		{
			assertThat(i(0).squareRoot(BY_EQUALITY, RoundingStrategies.floor())).isEqualTo(i(0));
		}

		@Test
		@DisplayName("one returns one")
		void oneReturnsOne()
		{
			assertThat(i(1).squareRoot(BY_EQUALITY, RoundingStrategies.floor())).isEqualTo(i(1));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("perfectSquareCases")
		@DisplayName("perfect square returns exact root")
		void perfectSquareReturnsExactRoot(final String as, final long radicand, final long expected)
		{
			assertThat(i(radicand).squareRoot(BY_EQUALITY, RoundingStrategies.floor()))
					.as(as).isEqualTo(i(expected));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("nonPerfectCeilingCases")
		@DisplayName("non-perfect square converges to stable estimate with ceiling rounding")
		void nonPerfectSquareConvergesToStableEstimate(final String as, final long radicand, final long expected)
		{
			assertThat(i(radicand).squareRoot(BY_EQUALITY, RoundingStrategies.ceiling()))
					.as(as).isEqualTo(i(expected));
		}

		@Test
		@DisplayName("squareRoot is equivalent to root with degree 2")
		void squareRootIsEquivalentToRootWithDegreeTwo()
		{
			assertThat(i(25).squareRoot(BY_EQUALITY, RoundingStrategies.floor()))
					.isEqualTo(i(25).root(2, BY_EQUALITY, RoundingStrategies.floor()));
		}

		private static Stream<Arguments> perfectSquareCases()
		{
			return Stream.of(
					Arguments.of("√4 = 2", 4, 2),
					Arguments.of("√9 = 3", 9, 3),
					Arguments.of("√16 = 4", 16, 4),
					Arguments.of("√25 = 5", 25, 5),
					Arguments.of("√100 = 10", 100, 10)
			);
		}

		private static Stream<Arguments> nonPerfectCeilingCases()
		{
			return Stream.of(
					Arguments.of("√2 ceiling → 2", 2, 2),
					Arguments.of("√3 ceiling → 2", 3, 2),
					Arguments.of("√8 ceiling → 3", 8, 3)
			);
		}
	}

	@Nested
	@DisplayName("when computing integer cube roots")
	final class WhenComputingIntegerCubeRoots
	{
		@Test
		@DisplayName("zero returns zero")
		void zeroReturnsZero()
		{
			assertThat(i(0).root(3, BY_EQUALITY, RoundingStrategies.floor())).isEqualTo(i(0));
		}

		@Test
		@DisplayName("one returns one")
		void oneReturnsOne()
		{
			assertThat(i(1).root(3, BY_EQUALITY, RoundingStrategies.floor())).isEqualTo(i(1));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("perfectCubeCases")
		@DisplayName("perfect cube returns exact root")
		void perfectCubeReturnsExactRoot(final String as, final long radicand, final long expected)
		{
			assertThat(i(radicand).root(3, BY_EQUALITY, RoundingStrategies.floor()))
					.as(as).isEqualTo(i(expected));
		}

		@Test
		@DisplayName("non-perfect cube converges to stable floor estimate")
		void nonPerfectCubeFloor()
		{
			// ∛10 ≈ 2.154 — Newton with floor converges to 2
			assertThat(i(10).root(3, BY_EQUALITY, RoundingStrategies.floor())).isEqualTo(i(2));
		}

		@Test
		@DisplayName("non-perfect cube converges to stable ceiling estimate")
		void nonPerfectCubeCeiling()
		{
			// ∛10 ≈ 2.154 — Newton with ceiling converges to 3
			assertThat(i(10).root(3, BY_EQUALITY, RoundingStrategies.ceiling())).isEqualTo(i(3));
		}

		private static Stream<Arguments> perfectCubeCases()
		{
			return Stream.of(
					Arguments.of("∛8 = 2", 8, 2),
					Arguments.of("∛27 = 3", 27, 3),
					Arguments.of("∛64 = 4", 64, 4)
			);
		}
	}

	@Nested
	@DisplayName("when computing rational roots")
	final class WhenComputingRationalRoots
	{
		@Test
		@DisplayName("zero returns zero")
		void zeroReturnsZero()
		{
			assertThat(r(0, 1).squareRoot(WITHIN_THOUSANDTH, RATIONAL_ROUNDING)).isEqualTo(r(0, 1));
		}

		@Test
		@DisplayName("one returns one")
		void oneReturnsOne()
		{
			assertThat(r(1, 1).squareRoot(WITHIN_THOUSANDTH, RATIONAL_ROUNDING)).isEqualTo(r(1, 1));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("rationalPerfectSquareCases")
		@DisplayName("approximates square root of perfect rational square within tolerance")
		void approximatesPerfectRationalSquareWithinTolerance(final String as,
		                                                      final RationalNumber radicand,
		                                                      final RationalNumber expected)
		{
			final RationalNumber result = radicand.squareRoot(WITHIN_THOUSANDTH, RATIONAL_ROUNDING);
			assertWithinTolerance(as, result, expected, r(1, 1000));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("irrationalSquareCases")
		@DisplayName("irrational square root: result squared is within tolerance of radicand")
		void irrationalSquareRootSquaredIsClose(final String as, final RationalNumber radicand)
		{
			final RationalNumber result = radicand.squareRoot(WITHIN_THOUSANDTH, RATIONAL_ROUNDING);
			final double resultD = toDouble(result);
			final double radicandD = toDouble(radicand);
			assertThat(Math.abs(resultD * resultD - radicandD)).as(as).isLessThanOrEqualTo(1.0 / 50);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("highPrecisionCases")
		@DisplayName("converges to within 1/1_000_000 for high-precision requests")
		void convergesToHighPrecision(final String as, final RationalNumber radicand,
		                              final RationalNumber expected)
		{
			final RationalNumber result = radicand.squareRoot(WITHIN_MILLIONTH, RATIONAL_ROUNDING);
			assertWithinTolerance(as, result, expected, r(1, 1_000_000));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("cubeRootCases")
		@DisplayName("approximates cube root of perfect rational cube within tolerance")
		void approximatesPerfectRationalCube(final String as, final RationalNumber radicand,
		                                     final RationalNumber expected)
		{
			final RationalNumber result = radicand.root(3, WITHIN_THOUSANDTH, RATIONAL_ROUNDING);
			assertWithinTolerance(as, result, expected, r(1, 1000));
		}

		@Test
		@DisplayName("squareRoot is equivalent to root with degree 2")
		void squareRootIsEquivalentToRootWithDegreeTwo()
		{
			final RationalNumber radicand = r(4, 1);
			assertThat(radicand.squareRoot(WITHIN_THOUSANDTH, RATIONAL_ROUNDING))
					.isEqualTo(radicand.root(2, WITHIN_THOUSANDTH, RATIONAL_ROUNDING));
		}

		private static Stream<Arguments> rationalPerfectSquareCases()
		{
			return Stream.of(
					Arguments.of("√(4/1) ≈ 2", r(4, 1), r(2, 1)),
					Arguments.of("√(1/4) ≈ 1/2", r(1, 4), r(1, 2)),
					Arguments.of("√(1/9) ≈ 1/3", r(1, 9), r(1, 3)),
					Arguments.of("√(4/9) ≈ 2/3", r(4, 9), r(2, 3)),
					Arguments.of("√(9/25) ≈ 3/5", r(9, 25), r(3, 5)),
					Arguments.of("√(9/4) ≈ 3/2", r(9, 4), r(3, 2)),
					Arguments.of("√(25/4) ≈ 5/2", r(25, 4), r(5, 2)),
					Arguments.of("√(625/49) ≈ 25/7", r(625, 49), r(25, 7)),
					Arguments.of("√(49/169) ≈ 7/13", r(49, 169), r(7, 13)),
					Arguments.of("√(169/49) ≈ 13/7", r(169, 49), r(13, 7)),
					Arguments.of("√(25/1) ≈ 5", r(25, 1), r(5, 1)),
					Arguments.of("√(64/1) ≈ 8", r(64, 1), r(8, 1)),
					Arguments.of("√(100/1) ≈ 10", r(100, 1), r(10, 1)),
					Arguments.of("√(361/1) ≈ 19", r(361, 1), r(19, 1)),
					Arguments.of("√(2500/1) ≈ 50", r(2500, 1), r(50, 1)),
					Arguments.of("√(10000/1) ≈ 100", r(10_000, 1), r(100, 1)),
					Arguments.of("√(1000000/1) ≈ 1000", r(1_000_000, 1), r(1000, 1)),
					Arguments.of("√(1/100) ≈ 1/10", r(1, 100), r(1, 10)),
					Arguments.of("√(1/1000000) ≈ 1/1000", r(1, 1_000_000), r(1, 1000))
			);
		}

		private static Stream<Arguments> irrationalSquareCases()
		{
			return Stream.of(
					Arguments.of("√2", r(2, 1)),
					Arguments.of("√3", r(3, 1)),
					Arguments.of("√5", r(5, 1)),
					Arguments.of("√7", r(7, 1)),
					Arguments.of("√(1/2)", r(1, 2)),
					Arguments.of("√(2/3)", r(2, 3)),
					Arguments.of("√(7/3)", r(7, 3)),
					Arguments.of("√(22/7)", r(22, 7))
			);
		}

		private static Stream<Arguments> highPrecisionCases()
		{
			return Stream.of(
					Arguments.of("√(4/1) to 10⁻⁶", r(4, 1), r(2, 1)),
					Arguments.of("√(9/1) to 10⁻⁶", r(9, 1), r(3, 1)),
					Arguments.of("√(100/1) to 10⁻⁶", r(100, 1), r(10, 1)),
					Arguments.of("√(10000/1) to 10⁻⁶", r(10_000, 1), r(100, 1)),
					Arguments.of("√(49/169) to 10⁻⁶", r(49, 169), r(7, 13)),
					Arguments.of("√(1/100) to 10⁻⁶", r(1, 100), r(1, 10))
			);
		}

		private static Stream<Arguments> cubeRootCases()
		{
			return Stream.of(
					Arguments.of("∛(8/1) = 2", r(8, 1), r(2, 1)),
					Arguments.of("∛(27/1) = 3", r(27, 1), r(3, 1)),
					Arguments.of("∛(125/1) = 5", r(125, 1), r(5, 1)),
					Arguments.of("∛(27/8) = 3/2", r(27, 8), r(3, 2)),
					Arguments.of("∛(1/27) = 1/3", r(1, 27), r(1, 3)),
					Arguments.of("∛(8/125) = 2/5", r(8, 125), r(2, 5))
			);
		}
	}

	@Nested
	@DisplayName("when degree is invalid")
	final class WhenDegreeIsInvalid
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("invalidDegreeCases")
		@DisplayName("throws IllegalArgumentException")
		void throwsIllegalArgumentException(final String as, final int degree)
		{
			assertThatThrownBy(() -> i(4).root(degree, BY_EQUALITY, RoundingStrategies.floor()))
					.as(as)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining(String.valueOf(degree));
		}

		private static Stream<Arguments> invalidDegreeCases()
		{
			return Stream.of(
					Arguments.of("degree 1 is rejected", 1),
					Arguments.of("degree 0 is rejected", 0),
					Arguments.of("negative degree is rejected", -1)
			);
		}
	}
}
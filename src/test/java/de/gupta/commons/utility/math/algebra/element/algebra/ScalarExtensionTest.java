package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScalarExtension")
final class ScalarExtensionTest
{
	private static final ScalarExtension<IntegralNumber, RationalNumber> EXTENSION =
			n -> RationalNumberFactory.of(n, n.one());

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	private static RationalNumber r(final long num, final long denom)
	{
		return RationalNumberFactory.of(num, denom);
	}

	@Nested
	@DisplayName("when embedding")
	final class WhenEmbedding
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("embedCases")
		@DisplayName("lifts IntegralNumber to exact rational n/1")
		void liftsToExactRational(final String as, final IntegralNumber input, final RationalNumber expected)
		{
			assertThat(EXTENSION.embed(input)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("embed is injective: distinct integers map to distinct rationals")
		void embedIsInjective()
		{
			assertThat(EXTENSION.embed(i(3))).isNotEqualTo(EXTENSION.embed(i(4)));
			assertThat(EXTENSION.embed(i(-1))).isNotEqualTo(EXTENSION.embed(i(1)));
		}

		private static Stream<Arguments> embedCases()
		{
			return Stream.of(
					Arguments.of("embed(0) = 0/1", i(0), r(0, 1)),
					Arguments.of("embed(1) = 1/1", i(1), r(1, 1)),
					Arguments.of("embed(3) = 3/1", i(3), r(3, 1)),
					Arguments.of("embed(-5) = -5/1", i(-5), r(-5, 1)),
					Arguments.of("embed(100) = 100/1", i(100), r(100, 1))
			);
		}
	}

	@Nested
	@DisplayName("when accumulating in the extension space")
	final class WhenAccumulatingInExtensionSpace
	{
		@Test
		@DisplayName("addition of embedded values is exact")
		void additionOfEmbeddedValuesIsExact()
		{
			final RationalNumber sum = EXTENSION.embed(i(3)).add(EXTENSION.embed(i(4)));
			assertThat(sum).isEqualTo(EXTENSION.embed(i(7)));
		}

		@Test
		@DisplayName("scalar multiplication stays exact (no rounding mid-computation)")
		void scalarMultiplicationStaysExact()
		{
			final RationalNumber alpha = r(1, 3);
			final RationalNumber scaled = EXTENSION.embed(i(9)).multiply(alpha);
			assertThat(scaled).isEqualTo(r(3, 1));
		}

		@Test
		@DisplayName("one-third of three values accumulates exactly before projection")
		void onethirdOfThreeValuesAccumulatesExactly()
		{
			final RationalNumber alpha = r(1, 3);
			final RationalNumber a = EXTENSION.embed(i(10)).multiply(alpha);
			final RationalNumber b = EXTENSION.embed(i(20)).multiply(alpha);
			final RationalNumber c = EXTENSION.embed(i(30)).multiply(alpha);
			assertThat(a.add(b).add(c)).isEqualTo(r(20, 1));
		}

		@Test
		@DisplayName("EMA-style accumulation: alpha*x + (1-alpha)*prev stays exact")
		void emaStyleAccumulationStaysExact()
		{
			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			RationalNumber ema = EXTENSION.embed(i(100));
			ema = alpha.multiply(EXTENSION.embed(i(200))).add(oneMinusAlpha.multiply(ema));
			ema = alpha.multiply(EXTENSION.embed(i(100))).add(oneMinusAlpha.multiply(ema));

			assertThat(ema).isEqualTo(r(125, 1));
		}
	}

	@Nested
	@DisplayName("when projecting")
	final class WhenProjecting
	{
		@Test
		@DisplayName("project with floor rounding")
		void projectWithFloorRounding()
		{
			final ProjectionPolicy<RationalNumber, IntegralNumber> floorPolicy =
					r -> IntegralNumberFactory.of(Math.floorDiv(r.numerator().value(), r.denominator().value()));

			assertThat(EXTENSION.project(r(7, 2), floorPolicy)).isEqualTo(i(3));
			assertThat(EXTENSION.project(r(1, 3), floorPolicy)).isEqualTo(i(0));
			assertThat(EXTENSION.project(r(-7, 2), floorPolicy)).isEqualTo(i(-4));
		}

		@Test
		@DisplayName("project with round-half-up")
		void projectWithRoundHalfUp()
		{
			final ProjectionPolicy<RationalNumber, IntegralNumber> roundPolicy =
					r -> IntegralNumberFactory.of(Math.round(
							(double) r.numerator().value() / r.denominator().value()));

			assertThat(EXTENSION.project(r(7, 2), roundPolicy)).isEqualTo(i(4));
			assertThat(EXTENSION.project(r(3, 2), roundPolicy)).isEqualTo(i(2));
			assertThat(EXTENSION.project(r(1, 3), roundPolicy)).isEqualTo(i(0));
		}

		@Test
		@DisplayName("round-trip: project(embed(n), floor) = n for integers")
		void roundTripForIntegers()
		{
			final ProjectionPolicy<RationalNumber, IntegralNumber> floorPolicy =
					r -> IntegralNumberFactory.of(Math.floorDiv(r.numerator().value(), r.denominator().value()));

			assertThat(EXTENSION.project(EXTENSION.embed(i(42)), floorPolicy)).isEqualTo(i(42));
			assertThat(EXTENSION.project(EXTENSION.embed(i(-7)), floorPolicy)).isEqualTo(i(-7));
			assertThat(EXTENSION.project(EXTENSION.embed(i(0)), floorPolicy)).isEqualTo(i(0));
		}

		@Test
		@DisplayName("EMA result projected once produces stable value")
		void emaResultProjectedOnce()
		{
			final ProjectionPolicy<RationalNumber, IntegralNumber> floorPolicy =
					r -> IntegralNumberFactory.of(Math.floorDiv(r.numerator().value(), r.denominator().value()));

			final RationalNumber alpha = r(1, 2);
			final RationalNumber oneMinusAlpha = r(1, 1).subtract(alpha);

			RationalNumber ema = EXTENSION.embed(i(100));
			ema = alpha.multiply(EXTENSION.embed(i(200))).add(oneMinusAlpha.multiply(ema));
			ema = alpha.multiply(EXTENSION.embed(i(300))).add(oneMinusAlpha.multiply(ema));

			assertThat(EXTENSION.project(ema, floorPolicy)).isEqualTo(i(225));
		}
	}
}
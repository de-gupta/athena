package de.gupta.commons.utility.math.algebra.structure.module;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ScalarQuotientableStructure")
final class ScalarQuotientableStructureTest
{
	private static final ScalarQuotientableStructure<IntegralNumber, RationalNumber> STRUCTURE =
			RationalNumberFactory::of;

	private static IntegralNumber i(final long v)
	{
		return IntegralNumberFactory.of(v);
	}

	private static RationalNumber r(final long num, final long denom)
	{
		return RationalNumberFactory.of(num, denom);
	}

	@Nested
	@DisplayName("when computing ratio")
	final class WhenComputingRatio
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("ratioCases")
		@DisplayName("returns correct rational ratio")
		void returnsCorrectRationalRatio(final String as, final IntegralNumber numerator,
		                                 final IntegralNumber denominator, final RationalNumber expected)
		{
			assertThat(STRUCTURE.ratio(numerator, denominator)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("ratio of equal values is one")
		void ratioOfEqualValuesIsOne()
		{
			assertThat(STRUCTURE.ratio(i(7), i(7))).isEqualTo(r(1, 1));
			assertThat(STRUCTURE.ratio(i(-3), i(-3))).isEqualTo(r(1, 1));
		}

		@Test
		@DisplayName("result is normalized")
		void resultIsNormalized()
		{
			assertThat(STRUCTURE.ratio(i(6), i(4))).isEqualTo(r(3, 2));
			assertThat(STRUCTURE.ratio(i(-6), i(4))).isEqualTo(r(-3, 2));
		}

		@Test
		@DisplayName("throws for zero denominator")
		void throwsForZeroDenominator()
		{
			assertThatThrownBy(() -> STRUCTURE.ratio(i(5), i(0)))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("usable as lambda and method reference")
		void usableAsLambdaAndMethodReference()
		{
			final ScalarQuotientableStructure<IntegralNumber, RationalNumber> lambda =
					RationalNumberFactory::of;
			assertThat(lambda.ratio(i(3), i(4))).isEqualTo(r(3, 4));
		}

		private static Stream<Arguments> ratioCases()
		{
			return Stream.of(
					Arguments.of("3 / 4 = 3/4", i(3), i(4), r(3, 4)),
					Arguments.of("10 / 3 = 10/3", i(10), i(3), r(10, 3)),
					Arguments.of("1 / 3 = 1/3 (exact, no rounding)", i(1), i(3), r(1, 3)),
					Arguments.of("120 / 100 = 6/5", i(120), i(100), r(6, 5)),
					Arguments.of("-6 / 9 = -2/3", i(-6), i(9), r(-2, 3)),
					Arguments.of("5 / 1 = 5", i(5), i(1), r(5, 1))
			);
		}
	}
}
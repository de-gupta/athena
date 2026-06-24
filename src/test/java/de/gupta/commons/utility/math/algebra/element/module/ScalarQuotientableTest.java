package de.gupta.commons.utility.math.algebra.element.module;

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

@DisplayName("ScalarQuotientable")
final class ScalarQuotientableTest
{
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
		@DisplayName("returns correct scalar ratio")
		void returnsCorrectScalarRatio(final String as, final RationalNumber a, final RationalNumber b,
		                               final RationalNumber expected)
		{
			assertThat(a.ratio(b)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("ratio to self is one")
		void ratioToSelfIsOne()
		{
			assertThat(r(3, 7).ratio(r(3, 7))).isEqualTo(r(1, 1));
			assertThat(r(-2, 5).ratio(r(-2, 5))).isEqualTo(r(1, 1));
		}

		@Test
		@DisplayName("throws for zero denominator")
		void throwsForZeroDenominator()
		{
			assertThatThrownBy(() -> r(1, 2).ratio(r(0, 1)))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		@DisplayName("ratio chains: c.ratio(a) = c.ratio(b).multiply(b.ratio(a))")
		void ratioChainsCorrectly()
		{
			final RationalNumber a = r(1, 2);
			final RationalNumber b = r(2, 3);
			final RationalNumber c = r(3, 4);
			assertThat(c.ratio(a)).isEqualTo(c.ratio(b).multiply(b.ratio(a)));
		}

		private static Stream<Arguments> ratioCases()
		{
			return Stream.of(
					Arguments.of("(3/4) / (1/2) = 3/2", r(3, 4), r(1, 2), r(3, 2)),
					Arguments.of("(1/2) / (3/4) = 2/3", r(1, 2), r(3, 4), r(2, 3)),
					Arguments.of("(2/3) / (2/3) = 1", r(2, 3), r(2, 3), r(1, 1)),
					Arguments.of("(1/3) / (1/6) = 2", r(1, 3), r(1, 6), r(2, 1)),
					Arguments.of("(-1/2) / (1/4) = -2", r(-1, 2), r(1, 4), r(-2, 1)),
					Arguments.of("(1/4) / (-1/2) = -1/2", r(1, 4), r(-1, 2), r(-1, 2))
			);
		}
	}
}
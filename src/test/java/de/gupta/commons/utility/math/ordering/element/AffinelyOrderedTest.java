package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AffinelyOrdered")
final class AffinelyOrderedTest
{
	private static IntegralNumbers i(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	@Nested
	@DisplayName("when computing displacement")
	final class WhenComputingDisplacement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("displacementCases")
		@DisplayName("returns signed displacement from this to other")
		void returnsSignedDisplacementFromThisToOther(final String as,
		                                              final IntegralNumbers from,
		                                              final IntegralNumbers to,
		                                              final IntegralNumbers expected)
		{
			assertThat(from.displacementTo(to)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> displacementCases()
		{
			return Stream.of(
					Arguments.of("3 to 7 = +4", i(3), i(7), i(4)),
					Arguments.of("7 to 3 = -4", i(7), i(3), i(-4)),
					Arguments.of("5 to 5 = 0", i(5), i(5), i(0)),
					Arguments.of("-3 to 3 = 6", i(-3), i(3), i(6))
			);
		}
	}

	@Nested
	@DisplayName("when translating")
	final class WhenTranslating
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("translateCases")
		@DisplayName("returns point shifted by displacement")
		void returnsPointShiftedByDisplacement(final String as,
		                                       final IntegralNumbers point,
		                                       final IntegralNumbers displacement,
		                                       final IntegralNumbers expected)
		{
			assertThat(point.translate(displacement)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> translateCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", i(3), i(4), i(7)),
					Arguments.of("7 + (-4) = 3", i(7), i(-4), i(3)),
					Arguments.of("5 + 0 = 5", i(5), i(0), i(5))
			);
		}
	}

	@Nested
	@DisplayName("when combining displacement and translation")
	final class WhenCombiningDisplacementAndTranslation
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("translate by displacement returns the target")
		void translateByDisplacementReturnsTarget(final String as,
		                                          final IntegralNumbers from,
		                                          final IntegralNumbers to)
		{
			assertThat(from.translate(from.displacementTo(to))).as(as).isEqualTo(to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("displacement is antisymmetric")
		void displacementIsAntisymmetric(final String as,
		                                 final IntegralNumbers from,
		                                 final IntegralNumbers to)
		{
			assertThat(from.displacementTo(to)).as(as).isEqualTo(to.displacementTo(from).negate());
		}

		private static Stream<Arguments> roundTripCases()
		{
			return Stream.of(
					Arguments.of("3 → 7", i(3), i(7)),
					Arguments.of("7 → 3", i(7), i(3)),
					Arguments.of("0 → 0", i(0), i(0)),
					Arguments.of("-5 → 5", i(-5), i(5))
			);
		}
	}

	@Nested
	@DisplayName("when verifying affine space laws")
	final class WhenVerifyingAffineLaws
	{
		@Test
		@DisplayName("translate by zero is identity")
		void translateByZeroIsIdentity()
		{
			assertThat(i(5).translate(i(0))).isEqualTo(i(5));
			assertThat(i(-3).translate(i(0))).isEqualTo(i(-3));
			assertThat(i(0).translate(i(0))).isEqualTo(i(0));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("orderCorrelationCases")
		@DisplayName("displacement sign correlates with order: a < b iff displacement is positive")
		void displacementSignCorrelatesWithOrder(final String as,
		                                         final IntegralNumbers from,
		                                         final IntegralNumbers to,
		                                         final boolean expectedPositive,
		                                         final boolean expectedNegative)
		{
			assertThat(from.displacementTo(to).isPositive()).as("%s: positive", as).isEqualTo(expectedPositive);
			assertThat(from.displacementTo(to).isNegative()).as("%s: negative", as).isEqualTo(expectedNegative);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("additivityCases")
		@DisplayName("displacement is additive: between(a,c) = between(a,b) + between(b,c)")
		void displacementIsAdditive(final String as,
		                            final IntegralNumbers a,
		                            final IntegralNumbers b,
		                            final IntegralNumbers c)
		{
			assertThat(a.displacementTo(c)).as(as)
			                               .isEqualTo(a.displacementTo(b).add(b.displacementTo(c)));
		}

		private static Stream<Arguments> orderCorrelationCases()
		{
			return Stream.of(
					Arguments.of("3 < 7: positive displacement", i(3), i(7), true, false),
					Arguments.of("7 > 3: negative displacement", i(7), i(3), false, true),
					Arguments.of("-5 < 0: positive displacement", i(-5), i(0), true, false),
					Arguments.of("0 > -5: negative displacement", i(0), i(-5), false, true)
			);
		}

		private static Stream<Arguments> additivityCases()
		{
			return Stream.of(
					Arguments.of("1→3→7: (1→7) = (1→3)+(3→7)", i(1), i(3), i(7)),
					Arguments.of("7→3→1: (7→1) = (7→3)+(3→1)", i(7), i(3), i(1)),
					Arguments.of("-5→0→5: (-5→5) = (-5→0)+(0→5)", i(-5), i(0), i(5)),
					Arguments.of("equal points: all displacements zero", i(4), i(4), i(4))
			);
		}
	}
}
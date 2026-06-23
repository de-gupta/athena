package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AffinelyOrdered")
final class AffinelyOrderedTest
{
	private static IntegersAsEuclideanDomain i(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when computing displacement")
	final class WhenComputingDisplacement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("displacementCases")
		@DisplayName("returns signed displacement from this to other")
		void returnsSignedDisplacementFromThisToOther(final String as,
		                                              final IntegersAsEuclideanDomain from,
		                                              final IntegersAsEuclideanDomain to,
		                                              final IntegersAsEuclideanDomain expected)
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
		                                       final IntegersAsEuclideanDomain point,
		                                       final IntegersAsEuclideanDomain displacement,
		                                       final IntegersAsEuclideanDomain expected)
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
		                                          final IntegersAsEuclideanDomain from,
		                                          final IntegersAsEuclideanDomain to)
		{
			assertThat(from.translate(from.displacementTo(to))).as(as).isEqualTo(to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("displacement is antisymmetric")
		void displacementIsAntisymmetric(final String as,
		                                 final IntegersAsEuclideanDomain from,
		                                 final IntegersAsEuclideanDomain to)
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
}
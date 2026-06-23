package de.gupta.commons.utility.math.ordering.structure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AffineOrderStructure")
final class AffineOrderStructureTest
{
	private static final AffineOrderStructure<Integer, Integer> INTEGERS =
			AffineOrderStructure.of(IntegerNaturalOrder.INSTANCE, (from, to) -> to - from, Integer::sum);

	@Nested
	@DisplayName("when computing displacement")
	final class WhenComputingDisplacement
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("displacementCases")
		@DisplayName("returns signed displacement from to to")
		void returnsSignedDisplacement(final String as, final int from, final int to, final int expected)
		{
			assertThat(INTEGERS.between(from, to)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> displacementCases()
		{
			return Stream.of(
					Arguments.of("3 to 7 = +4", 3, 7, 4),
					Arguments.of("7 to 3 = -4", 7, 3, -4),
					Arguments.of("5 to 5 = 0", 5, 5, 0),
					Arguments.of("-3 to 3 = 6", -3, 3, 6)
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
		void returnsPointShiftedByDisplacement(final String as, final int point, final int displacement,
		                                       final int expected)
		{
			assertThat(INTEGERS.translate(point, displacement)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> translateCases()
		{
			return Stream.of(
					Arguments.of("3 + 4 = 7", 3, 4, 7),
					Arguments.of("7 + (-4) = 3", 7, -4, 3),
					Arguments.of("5 + 0 = 5", 5, 0, 5)
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
		void translateByDisplacementReturnsTarget(final String as, final int from, final int to)
		{
			assertThat(INTEGERS.translate(from, INTEGERS.between(from, to))).as(as).isEqualTo(to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("displacement is antisymmetric")
		void displacementIsAntisymmetric(final String as, final int from, final int to)
		{
			assertThat(INTEGERS.between(from, to)).as(as).isEqualTo(-INTEGERS.between(to, from));
		}

		private static Stream<Arguments> roundTripCases()
		{
			return Stream.of(
					Arguments.of("3 → 7", 3, 7),
					Arguments.of("7 → 3", 7, 3),
					Arguments.of("0 → 0", 0, 0),
					Arguments.of("-5 → 5", -5, 5)
			);
		}
	}
}
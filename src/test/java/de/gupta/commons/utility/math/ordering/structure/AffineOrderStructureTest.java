package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
			assertThat(INTEGERS.displacement(from, to)).as(as).isEqualTo(expected);
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
			assertThat(INTEGERS.translate(from, INTEGERS.displacement(from, to))).as(as).isEqualTo(to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("displacement is antisymmetric")
		void displacementIsAntisymmetric(final String as, final int from, final int to)
		{
			assertThat(INTEGERS.displacement(from, to)).as(as).isEqualTo(-INTEGERS.displacement(to, from));
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

	@Nested
	@DisplayName("when verifying affine space laws (integers)")
	final class WhenVerifyingAffineLaws
	{
		@Test
		@DisplayName("translate by zero is identity")
		void translateByZeroIsIdentity()
		{
			assertThat(INTEGERS.translate(5, 0)).isEqualTo(5);
			assertThat(INTEGERS.translate(-3, 0)).isEqualTo(-3);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("orderCorrelationCases")
		@DisplayName("displacement sign correlates with order")
		void displacementSignCorrelatesWithOrder(final String as, final int from, final int to,
		                                         final boolean expectedPositive)
		{
			int d = INTEGERS.displacement(from, to);
			assertThat(d > 0).as("%s: positive", as).isEqualTo(expectedPositive);
			assertThat(d < 0).as("%s: negative", as).isEqualTo(!expectedPositive && from != to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("additivityCases")
		@DisplayName("displacement is additive: between(a,c) = between(a,b) + between(b,c)")
		void displacementIsAdditive(final String as, final int a, final int b, final int c)
		{
			assertThat(INTEGERS.displacement(a, c)).as(as)
			                                       .isEqualTo(
					                                       INTEGERS.displacement(a, b) + INTEGERS.displacement(b, c));
		}

		private static Stream<Arguments> orderCorrelationCases()
		{
			return Stream.of(
					Arguments.of("1 < 5", 1, 5, true),
					Arguments.of("5 > 1", 5, 1, false),
					Arguments.of("-3 < 3", -3, 3, true),
					Arguments.of("0 > -7", 0, -7, false)
			);
		}

		private static Stream<Arguments> additivityCases()
		{
			return Stream.of(
					Arguments.of("1→3→7", 1, 3, 7),
					Arguments.of("7→3→1", 7, 3, 1),
					Arguments.of("-5→0→5", -5, 0, 5),
					Arguments.of("degenerate: 4→4→4", 4, 4, 4)
			);
		}
	}

	@Nested
	@DisplayName("with LocalDate and long-days displacement (E ≠ D)")
	final class WithLocalDateDisplacement
	{
		private static final AffineOrderStructure<LocalDate, Long> DATES = AffineOrderStructure.of(
				(a, b) ->
				{
					int c = a.compareTo(b);
					return c < 0 ? OrderRelation.LESS_THAN : c > 0 ? OrderRelation.GREATER_THAN : OrderRelation.EQUAL;
				},
				ChronoUnit.DAYS::between,
				LocalDate::plusDays);

		private static final LocalDate JAN_01 = LocalDate.of(2024, 1, 1);
		private static final LocalDate JAN_10 = LocalDate.of(2024, 1, 10);
		private static final LocalDate FEB_28 = LocalDate.of(2024, 2, 28);
		private static final LocalDate FEB_29 = LocalDate.of(2024, 2, 29);
		private static final LocalDate MAR_01 = LocalDate.of(2024, 3, 1);
		private static final LocalDate DEC_31 = LocalDate.of(2023, 12, 31);

		@ParameterizedTest(name = "{0}")
		@MethodSource("betweenCases")
		@DisplayName("between returns exact signed day count")
		void betweenReturnsExactSignedDayCount(final String as, final LocalDate from, final LocalDate to,
		                                       final long expected)
		{
			assertThat(DATES.displacement(from, to)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("translateCases")
		@DisplayName("translate returns correct date")
		void translateReturnsCorrectDate(final String as, final LocalDate point, final long days,
		                                 final LocalDate expected)
		{
			assertThat(DATES.translate(point, days)).as(as).isEqualTo(expected);
		}

		@Test
		@DisplayName("translate by zero is identity")
		void translateByZeroIsIdentity()
		{
			assertThat(DATES.translate(JAN_01, 0L)).isEqualTo(JAN_01);
			assertThat(DATES.translate(FEB_29, 0L)).isEqualTo(FEB_29);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("translate by displacement returns the target")
		void translateByDisplacementReturnsTarget(final String as, final LocalDate from, final LocalDate to)
		{
			assertThat(DATES.translate(from, DATES.displacement(from, to))).as(as).isEqualTo(to);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("roundTripCases")
		@DisplayName("displacement is antisymmetric")
		void displacementIsAntisymmetric(final String as, final LocalDate from, final LocalDate to)
		{
			assertThat(DATES.displacement(from, to)).as(as).isEqualTo(-DATES.displacement(to, from));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("additivityCases")
		@DisplayName("displacement is additive: between(a,c) = between(a,b) + between(b,c)")
		void displacementIsAdditive(final String as, final LocalDate a, final LocalDate b, final LocalDate c)
		{
			assertThat(DATES.displacement(a, c)).as(as)
			                                    .isEqualTo(DATES.displacement(a, b) + DATES.displacement(b, c));
		}

		private static Stream<Arguments> betweenCases()
		{
			return Stream.of(
					Arguments.of("Jan 1 → Jan 10 = 9 days", JAN_01, JAN_10, 9L),
					Arguments.of("Jan 10 → Jan 1 = -9 days", JAN_10, JAN_01, -9L),
					Arguments.of("same date = 0", JAN_01, JAN_01, 0L),
					Arguments.of("Dec 31 → Jan 1 = 1 day (year boundary)", DEC_31, JAN_01, 1L),
					Arguments.of("Feb 28 → Feb 29 = 1 day (leap day exists)", FEB_28, FEB_29, 1L),
					Arguments.of("Feb 29 → Mar 1 = 1 day (after leap day)", FEB_29, MAR_01, 1L),
					Arguments.of("Feb 28 → Mar 1 = 2 days (through leap day)", FEB_28, MAR_01, 2L)
			);
		}

		private static Stream<Arguments> translateCases()
		{
			return Stream.of(
					Arguments.of("Jan 1 + 9 = Jan 10", JAN_01, 9L, JAN_10),
					Arguments.of("Jan 10 + (-9) = Jan 1", JAN_10, -9L, JAN_01),
					Arguments.of("Feb 28 + 1 = Feb 29 (leap year)", FEB_28, 1L, FEB_29),
					Arguments.of("Feb 28 + 2 = Mar 1 (through leap day)", FEB_28, 2L, MAR_01),
					Arguments.of("Jan 1 + (-1) = Dec 31 (backwards year boundary)", JAN_01, -1L, DEC_31),
					Arguments.of("Mar 1 + (-2) = Feb 28 (backwards through leap day)", MAR_01, -2L, FEB_28)
			);
		}

		private static Stream<Arguments> roundTripCases()
		{
			return Stream.of(
					Arguments.of("Jan 1 → Jan 10", JAN_01, JAN_10),
					Arguments.of("Jan 10 → Jan 1", JAN_10, JAN_01),
					Arguments.of("Feb 28 → Mar 1 (leap boundary)", FEB_28, MAR_01),
					Arguments.of("Dec 31 → Jan 1 (year boundary)", DEC_31, JAN_01)
			);
		}

		private static Stream<Arguments> additivityCases()
		{
			return Stream.of(
					Arguments.of("Jan1→Jan5→Jan10: 4+5=9", JAN_01,
							LocalDate.of(2024, 1, 5), JAN_10),
					Arguments.of("Jan1→Feb28→Mar1: crosses leap day", JAN_01, FEB_28, MAR_01),
					Arguments.of("Dec31→Jan1→Jan10: crosses year boundary", DEC_31, JAN_01, JAN_10)
			);
		}
	}
}
package de.gupta.commons.utility.math.ordering;

import de.gupta.commons.utility.comparison.ComparisonResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static de.gupta.commons.utility.math.ordering.OrderRelation.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderRelation")
final class OrderRelationTest
{
	@Nested
	@DisplayName("when classifying position")
	final class WhenClassifyingPosition
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("reportsCorrectPositionFlagsCases")
		@DisplayName("reports correct position flags")
		void reportsCorrectPositionFlags(final String as, final OrderRelation value,
		                                 final boolean expectedLt, final boolean expectedLeq,
		                                 final boolean expectedEq, final boolean expectedGt, final boolean expectedGeq)
		{
			assertThat(value.isLessThan()).as("%s: isLessThan", as).isEqualTo(expectedLt);
			assertThat(value.isLessThanOrEqualTo()).as("%s: isLessThanOrEqualTo", as).isEqualTo(expectedLeq);
			assertThat(value.isEqualTo()).as("%s: isEqualTo", as).isEqualTo(expectedEq);
			assertThat(value.isGreaterThan()).as("%s: isGreaterThan", as).isEqualTo(expectedGt);
			assertThat(value.isGreaterThanOrEqualTo()).as("%s: isGreaterThanOrEqualTo", as).isEqualTo(expectedGeq);
		}

		private static Stream<Arguments> reportsCorrectPositionFlagsCases()
		{
			return Stream.of(
					new ClassificationCase("LESS_THAN", LESS_THAN, true, true, false, false, false),
					new ClassificationCase("EQUAL", EQUAL, false, true, true, false, true),
					new ClassificationCase("GREATER_THAN", GREATER_THAN, false, false, false, true, true),
					new ClassificationCase("INCOMPARABLE", INCOMPARABLE, false, false, false, false, false)
			).map(tc -> Arguments.of(tc.as(), tc.value(),
					tc.lt(), tc.leq(), tc.eq(), tc.gt(), tc.geq()));
		}

		private record ClassificationCase(String as, OrderRelation value,
		                                  boolean lt, boolean leq, boolean eq, boolean gt, boolean geq)
		{
		}
	}

	@Nested
	@DisplayName("when checking comparability")
	final class WhenCheckingComparability
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("isComparableAndIsIncomparableAreMutuallyExclusiveCases")
		@DisplayName("isComparable and isIncomparable are mutually exclusive")
		void isComparableAndIsIncomparableAreMutuallyExclusive(final String as, final OrderRelation value,
		                                                       final boolean expectedComparable)
		{
			assertThat(value.isComparable()).as("%s: isComparable", as).isEqualTo(expectedComparable);
			assertThat(value.isIncomparable()).as("%s: isIncomparable", as).isEqualTo(!expectedComparable);
		}

		private static Stream<Arguments> isComparableAndIsIncomparableAreMutuallyExclusiveCases()
		{
			return Stream.of(
					Arguments.of("LESS_THAN", LESS_THAN, true),
					Arguments.of("EQUAL", EQUAL, true),
					Arguments.of("GREATER_THAN", GREATER_THAN, true),
					Arguments.of("INCOMPARABLE", INCOMPARABLE, false)
			);
		}
	}

	@Nested
	@DisplayName("when bridging to ComparisonResult")
	final class WhenBridgingToComparisonResult
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("toComparisonResultReturnsPresentForComparableValuesAndEmptyForIncomparableCases")
		@DisplayName("toComparisonResult returns present for comparable values and empty for incomparable")
		void toComparisonResultReturnsPresentForComparableValuesAndEmptyForIncomparable(
				final String as, final OrderRelation value, final Optional<ComparisonResult> expected)
		{
			assertThat(value.toComparisonResult()).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("fromMapsEveryComparisonResultToItsOrderRelationCounterpartCases")
		@DisplayName("from maps every ComparisonResult to its OrderRelation counterpart")
		void fromMapsEveryComparisonResultToItsOrderRelationCounterpart(
				final String as, final ComparisonResult input, final OrderRelation expected)
		{
			assertThat(OrderRelation.from(input)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("fromIntMapsSignumToOrderRelationCases")
		@DisplayName("from(int) maps signum to the correct OrderRelation")
		void fromIntMapsSignumToOrderRelation(final String as, final int input, final OrderRelation expected)
		{
			assertThat(OrderRelation.from(input)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> toComparisonResultReturnsPresentForComparableValuesAndEmptyForIncomparableCases()
		{
			return Stream.of(
					Arguments.of("LESS_THAN maps to LESS_THAN", LESS_THAN, Optional.of(ComparisonResult.LESS_THAN)),
					Arguments.of("EQUAL maps to EQUAL", EQUAL, Optional.of(ComparisonResult.EQUAL)),
					Arguments.of("GREATER_THAN maps to GREATER_THAN", GREATER_THAN,
							Optional.of(ComparisonResult.GREATER_THAN)),
					Arguments.of("INCOMPARABLE maps to empty", INCOMPARABLE, Optional.empty())
			);
		}

		private static Stream<Arguments> fromMapsEveryComparisonResultToItsOrderRelationCounterpartCases()
		{
			return Stream.of(
					Arguments.of("LESS_THAN", ComparisonResult.LESS_THAN, LESS_THAN),
					Arguments.of("EQUAL", ComparisonResult.EQUAL, EQUAL),
					Arguments.of("GREATER_THAN", ComparisonResult.GREATER_THAN, GREATER_THAN)
			);
		}

		private static Stream<Arguments> fromIntMapsSignumToOrderRelationCases()
		{
			return Stream.of(
					Arguments.of("-1 maps to LESS_THAN", -1, LESS_THAN),
					Arguments.of("-42 maps to LESS_THAN", -42, LESS_THAN),
					Arguments.of("-1000 maps to LESS_THAN", -1000, LESS_THAN),
					Arguments.of("MIN_VALUE maps to LESS_THAN", Integer.MIN_VALUE, LESS_THAN),
					Arguments.of("0 maps to EQUAL", 0, EQUAL),
					Arguments.of("1 maps to GREATER_THAN", 1, GREATER_THAN),
					Arguments.of("42 maps to GREATER_THAN", 42, GREATER_THAN),
					Arguments.of("1000 maps to GREATER_THAN", 1000, GREATER_THAN),
					Arguments.of("MAX_VALUE maps to GREATER_THAN", Integer.MAX_VALUE, GREATER_THAN)
			);
		}
	}
}
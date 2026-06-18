package de.gupta.commons.utility.comparison;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ComparisonType")
final class ComparisonTypeTest
{
	@Nested
	@DisplayName("when checking compatibility with a ComparisonResult")
	final class WhenCheckingCompatibility
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("isCompatibleWithCases")
		@DisplayName("returns the correct result for each type/result combination")
		void returnsTheCorrectResult(final String as, final ComparisonType type,
		                             final ComparisonResult result, final boolean expected)
		{
			assertThat(type.isCompatibleWith(result)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> isCompatibleWithCases()
		{
			return Stream.of(
					// LESS_THAN
					Arguments.of("LESS_THAN is compatible with LESS_THAN", ComparisonType.LESS_THAN,
							ComparisonResult.LESS_THAN, true),
					Arguments.of("LESS_THAN is not compatible with EQUAL", ComparisonType.LESS_THAN,
							ComparisonResult.EQUAL, false),
					Arguments.of("LESS_THAN is not compatible with GREATER_THAN", ComparisonType.LESS_THAN,
							ComparisonResult.GREATER_THAN, false),

					// LESS_THAN_OR_EQUAL
					Arguments.of("LESS_THAN_OR_EQUAL is compatible with LESS_THAN", ComparisonType.LESS_THAN_OR_EQUAL,
							ComparisonResult.LESS_THAN, true),
					Arguments.of("LESS_THAN_OR_EQUAL is compatible with EQUAL", ComparisonType.LESS_THAN_OR_EQUAL,
							ComparisonResult.EQUAL, true),
					Arguments.of("LESS_THAN_OR_EQUAL is not compatible with GREATER_THAN",
							ComparisonType.LESS_THAN_OR_EQUAL, ComparisonResult.GREATER_THAN, false),

					// EQUAL
					Arguments.of("EQUAL is not compatible with LESS_THAN", ComparisonType.EQUAL,
							ComparisonResult.LESS_THAN, false),
					Arguments.of("EQUAL is compatible with EQUAL", ComparisonType.EQUAL, ComparisonResult.EQUAL, true),
					Arguments.of("EQUAL is not compatible with GREATER_THAN", ComparisonType.EQUAL,
							ComparisonResult.GREATER_THAN, false),

					// GREATER_THAN_OR_EQUAL
					Arguments.of("GREATER_THAN_OR_EQUAL is not compatible with LESS_THAN",
							ComparisonType.GREATER_THAN_OR_EQUAL, ComparisonResult.LESS_THAN, false),
					Arguments.of("GREATER_THAN_OR_EQUAL is compatible with EQUAL", ComparisonType.GREATER_THAN_OR_EQUAL,
							ComparisonResult.EQUAL, true),
					Arguments.of("GREATER_THAN_OR_EQUAL is compatible with GREATER_THAN",
							ComparisonType.GREATER_THAN_OR_EQUAL, ComparisonResult.GREATER_THAN, true),

					// GREATER_THAN
					Arguments.of("GREATER_THAN is not compatible with LESS_THAN", ComparisonType.GREATER_THAN,
							ComparisonResult.LESS_THAN, false),
					Arguments.of("GREATER_THAN is not compatible with EQUAL", ComparisonType.GREATER_THAN,
							ComparisonResult.EQUAL, false),
					Arguments.of("GREATER_THAN is compatible with GREATER_THAN", ComparisonType.GREATER_THAN,
							ComparisonResult.GREATER_THAN, true),

					// NOT_EQUAL
					Arguments.of("NOT_EQUAL is compatible with LESS_THAN", ComparisonType.NOT_EQUAL,
							ComparisonResult.LESS_THAN, true),
					Arguments.of("NOT_EQUAL is not compatible with EQUAL", ComparisonType.NOT_EQUAL,
							ComparisonResult.EQUAL, false),
					Arguments.of("NOT_EQUAL is compatible with GREATER_THAN", ComparisonType.NOT_EQUAL,
							ComparisonResult.GREATER_THAN, true)
			);
		}
	}

	@Nested
	@DisplayName("when comparing values")
	final class WhenComparingValues
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("compareIntegersCases")
		@DisplayName("compares integers correctly")
		void comparesIntegersCorrectly(final String as, final ComparisonType type,
		                               final int value, final int threshold, final boolean expected)
		{
			assertThat(type.compare(value, threshold, Integer::compare)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("compareStringsCases")
		@DisplayName("compares strings correctly")
		void comparesStringsCorrectly(final String as, final ComparisonType type,
		                              final String value, final String threshold, final boolean expected)
		{
			assertThat(type.compare(value, threshold, String::compareTo)).as(as).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("compareWithCustomComparatorCases")
		@DisplayName("respects the supplied comparator")
		void respectsTheSuppliedComparator(final String as, final ComparisonType type,
		                                   final String value, final String threshold, final boolean expected)
		{
			assertThat(type.compare(value, threshold, String.CASE_INSENSITIVE_ORDER)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> compareIntegersCases()
		{
			return Stream.of(
					Arguments.of("3 < 5 satisfies LESS_THAN", ComparisonType.LESS_THAN, 3, 5, true),
					Arguments.of("5 < 5 does not satisfy LESS_THAN", ComparisonType.LESS_THAN, 5, 5, false),
					Arguments.of("7 < 5 does not satisfy LESS_THAN", ComparisonType.LESS_THAN, 7, 5, false),

					Arguments.of("3 <= 5 satisfies LESS_THAN_OR_EQUAL", ComparisonType.LESS_THAN_OR_EQUAL, 3, 5, true),
					Arguments.of("5 <= 5 satisfies LESS_THAN_OR_EQUAL", ComparisonType.LESS_THAN_OR_EQUAL, 5, 5, true),
					Arguments.of("7 <= 5 does not satisfy LESS_THAN_OR_EQUAL", ComparisonType.LESS_THAN_OR_EQUAL, 7, 5,
							false),

					Arguments.of("5 == 5 satisfies EQUAL", ComparisonType.EQUAL, 5, 5, true),
					Arguments.of("3 == 5 does not satisfy EQUAL", ComparisonType.EQUAL, 3, 5, false),

					Arguments.of("7 >= 5 satisfies GREATER_THAN_OR_EQUAL", ComparisonType.GREATER_THAN_OR_EQUAL, 7, 5,
							true),
					Arguments.of("5 >= 5 satisfies GREATER_THAN_OR_EQUAL", ComparisonType.GREATER_THAN_OR_EQUAL, 5, 5,
							true),
					Arguments.of("3 >= 5 does not satisfy GREATER_THAN_OR_EQUAL", ComparisonType.GREATER_THAN_OR_EQUAL,
							3, 5, false),

					Arguments.of("7 > 5 satisfies GREATER_THAN", ComparisonType.GREATER_THAN, 7, 5, true),
					Arguments.of("5 > 5 does not satisfy GREATER_THAN", ComparisonType.GREATER_THAN, 5, 5, false),
					Arguments.of("3 > 5 does not satisfy GREATER_THAN", ComparisonType.GREATER_THAN, 3, 5, false),

					Arguments.of("3 != 5 satisfies NOT_EQUAL", ComparisonType.NOT_EQUAL, 3, 5, true),
					Arguments.of("5 != 5 does not satisfy NOT_EQUAL", ComparisonType.NOT_EQUAL, 5, 5, false),
					Arguments.of("7 != 5 satisfies NOT_EQUAL", ComparisonType.NOT_EQUAL, 7, 5, true)
			);
		}

		private static Stream<Arguments> compareStringsCases()
		{
			return Stream.of(
					Arguments.of("\"apple\" < \"banana\" satisfies LESS_THAN", ComparisonType.LESS_THAN, "apple",
							"banana", true),
					Arguments.of("\"banana\" < \"banana\" does not satisfy LESS_THAN", ComparisonType.LESS_THAN,
							"banana", "banana", false),
					Arguments.of("\"cherry\" < \"banana\" does not satisfy LESS_THAN", ComparisonType.LESS_THAN,
							"cherry", "banana", false),

					Arguments.of("\"apple\" == \"apple\" satisfies EQUAL", ComparisonType.EQUAL, "apple", "apple",
							true),
					Arguments.of("\"apple\" == \"banana\" does not satisfy EQUAL", ComparisonType.EQUAL, "apple",
							"banana", false),

					Arguments.of("\"cherry\" > \"banana\" satisfies GREATER_THAN", ComparisonType.GREATER_THAN,
							"cherry", "banana", true),
					Arguments.of("\"banana\" > \"banana\" does not satisfy GREATER_THAN", ComparisonType.GREATER_THAN,
							"banana", "banana", false),

					Arguments.of("\"apple\" != \"banana\" satisfies NOT_EQUAL", ComparisonType.NOT_EQUAL, "apple",
							"banana", true),
					Arguments.of("\"apple\" != \"apple\" does not satisfy NOT_EQUAL", ComparisonType.NOT_EQUAL, "apple",
							"apple", false)
			);
		}

		private static Stream<Arguments> compareWithCustomComparatorCases()
		{
			return Stream.of(
					Arguments.of("APPLE == apple case-insensitively satisfies EQUAL", ComparisonType.EQUAL, "APPLE",
							"apple", true),
					Arguments.of("APPLE == apple case-insensitively does not satisfy NOT_EQUAL",
							ComparisonType.NOT_EQUAL, "APPLE", "apple", false),
					Arguments.of("BANANA > apple case-insensitively satisfies GREATER_THAN",
							ComparisonType.GREATER_THAN, "BANANA", "apple", true),
					Arguments.of("APPLE < banana case-insensitively satisfies LESS_THAN", ComparisonType.LESS_THAN,
							"APPLE", "banana", true)
			);
		}
	}
}
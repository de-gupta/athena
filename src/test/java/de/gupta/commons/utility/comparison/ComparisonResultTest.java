package de.gupta.commons.utility.comparison;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ComparisonResult")
final class ComparisonResultTest
{
	@Nested
	@DisplayName("when constructed from a comparator result")
	final class WhenConstructedFromComparatorResult
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("fromComparatorResultCases")
		@DisplayName("maps to the correct constant")
		void mapsToTheCorrectConstant(final String as, final int input, final ComparisonResult expected)
		{
			assertThat(ComparisonResult.fromComparatorResult(input)).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> fromComparatorResultCases()
		{
			return Stream.of(
					Arguments.of("-1 maps to LESS_THAN", -1, ComparisonResult.LESS_THAN),
					Arguments.of("-42 maps to LESS_THAN", -42, ComparisonResult.LESS_THAN),
					Arguments.of("-1000 maps to LESS_THAN", -1000, ComparisonResult.LESS_THAN),
					Arguments.of("MIN_VALUE maps to LESS_THAN", Integer.MIN_VALUE, ComparisonResult.LESS_THAN),
					Arguments.of("0 maps to EQUAL", 0, ComparisonResult.EQUAL),
					Arguments.of("1 maps to GREATER_THAN", 1, ComparisonResult.GREATER_THAN),
					Arguments.of("42 maps to GREATER_THAN", 42, ComparisonResult.GREATER_THAN),
					Arguments.of("1000 maps to GREATER_THAN", 1000, ComparisonResult.GREATER_THAN),
					Arguments.of("MAX_VALUE maps to GREATER_THAN", Integer.MAX_VALUE, ComparisonResult.GREATER_THAN)
			);
		}
	}
}

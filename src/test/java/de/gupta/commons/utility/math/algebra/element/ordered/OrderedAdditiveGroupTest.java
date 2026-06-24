package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderedAdditiveGroup")
final class OrderedAdditiveGroupTest
{
	private static IntegralNumbers integer(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	@Nested
	@DisplayName("when classifying sign")
	final class WhenClassifyingSign
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("classifiesSignCases")
		@DisplayName("reports positive, negative, and non-strict sign predicates consistently")
		void reportsPositiveNegativeAndNonStrictSignPredicatesConsistently(
				final String as, final IntegralNumbers value, final boolean expectedPositive,
				final boolean expectedNegative, final boolean expectedNonPositive, final boolean expectedNonNegative,
				final int expectedSignum)
		{
			assertThat(value.isPositive()).as("%s: isPositive", as).isEqualTo(expectedPositive);
			assertThat(value.isNegative()).as("%s: isNegative", as).isEqualTo(expectedNegative);
			assertThat(value.isNonPositive()).as("%s: isNonPositive", as).isEqualTo(expectedNonPositive);
			assertThat(value.isNonNegative()).as("%s: isNonNegative", as).isEqualTo(expectedNonNegative);
			assertThat(value.signum()).as("%s: signum", as).isEqualTo(expectedSignum);
		}

		private static Stream<Arguments> classifiesSignCases()
		{
			return Stream.of(
					Arguments.of("positive number", integer(7), true, false, false, true, 1),
					Arguments.of("negative number", integer(-7), false, true, true, false, -1),
					Arguments.of("zero", integer(0), false, false, true, true, 0)
			);
		}
	}

	@Nested
	@DisplayName("when taking absolute value")
	final class WhenTakingAbsoluteValue
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNonNegativeMagnitudeCases")
		@DisplayName("returns the non-negative magnitude")
		void returnsTheNonNegativeMagnitude(final String as, final IntegralNumbers value,
		                                    final IntegralNumbers expected)
		{
			assertThat(value.abs()).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsNonNegativeMagnitudeCases()
		{
			return Stream.of(
					Arguments.of("abs(7) = 7", integer(7), integer(7)),
					Arguments.of("abs(-7) = 7", integer(-7), integer(7)),
					Arguments.of("abs(0) = 0", integer(0), integer(0))
			);
		}
	}

	@Nested
	@DisplayName("when extracting the positive part")
	final class WhenExtractingThePositivePart
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsPositivePartCases")
		@DisplayName("returns the value when non-negative and zero otherwise")
		void returnsTheValueWhenNonNegativeAndZeroOtherwise(final String as,
		                                                    final IntegralNumbers value,
		                                                    final IntegralNumbers expected)
		{
			assertThat(value.positivePart()).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsPositivePartCases()
		{
			return Stream.of(
					Arguments.of("positivePart(7) = 7", integer(7), integer(7)),
					Arguments.of("positivePart(-7) = 0", integer(-7), integer(0)),
					Arguments.of("positivePart(0) = 0", integer(0), integer(0))
			);
		}
	}

	@Nested
	@DisplayName("when extracting the negative part")
	final class WhenExtractingTheNegativePart
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNegativePartCases")
		@DisplayName("returns the magnitude when non-positive and zero otherwise")
		void returnsTheMagnitudeWhenNonPositiveAndZeroOtherwise(final String as,
		                                                        final IntegralNumbers value,
		                                                        final IntegralNumbers expected)
		{
			assertThat(value.negativePart()).as(as).isEqualTo(expected);
		}

		private static Stream<Arguments> returnsNegativePartCases()
		{
			return Stream.of(
					Arguments.of("negativePart(7) = 0", integer(7), integer(0)),
					Arguments.of("negativePart(-7) = 7", integer(-7), integer(7)),
					Arguments.of("negativePart(0) = 0", integer(0), integer(0))
			);
		}
	}
}
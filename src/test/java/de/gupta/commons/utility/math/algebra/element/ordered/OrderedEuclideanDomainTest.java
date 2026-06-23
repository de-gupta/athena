package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrderedEuclideanDomain")
final class OrderedEuclideanDomainTest
{
	private static IntegersAsEuclideanDomain integer(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when dividing by scalar")
	final class WhenDividingByScalar
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("dividesByNegativeScalarCases")
		@DisplayName("converts negative scalars and delegates to the rounding strategy")
		void convertsNegativeScalarsAndDelegatesToTheRoundingStrategy(final String as, final long dividend,
		                                                              final long scalar,
		                                                              final RoundingStrategy<IntegersAsEuclideanDomain> strategy,
		                                                              final long expectedQuotient,
		                                                              final long expectedRemainder)
		{
			DivisionResult<IntegersAsEuclideanDomain> result = integer(dividend).divide(scalar, strategy);

			assertThat(result.quotient()).as("%s: quotient", as).isEqualTo(integer(expectedQuotient));
			assertThat(result.remainder()).as("%s: remainder", as).isEqualTo(integer(expectedRemainder));
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("throwsForZeroScalarCases")
		@DisplayName("throws for zero scalar after scalar-to-element conversion")
		void throwsForZeroScalarAfterScalarToElementConversion(final String as, final long dividend,
		                                                       final RoundingStrategy<IntegersAsEuclideanDomain> strategy)
		{
			assertThatThrownBy(() -> integer(dividend).divide(0L, strategy))
					.as(as)
					.isInstanceOf(ArithmeticException.class);
		}

		private static Stream<Arguments> dividesByNegativeScalarCases()
		{
			return Stream.of(
					Arguments.of("floor with negative scalar", 7L, -3L, RoundingStrategies.floor(), -3L, -2L),
					Arguments.of("ceiling with negative scalar", 7L, -3L, RoundingStrategies.ceiling(), -2L, 1L),
					Arguments.of("truncate with negative scalar", 7L, -3L, RoundingStrategies.truncate(), -2L, 1L),
					Arguments.of("exact division with negative scalar", 60L, -3L, RoundingStrategies.floor(), -20L, 0L)
			);
		}

		private static Stream<Arguments> throwsForZeroScalarCases()
		{
			return Stream.of(
					Arguments.of("floor with zero scalar", 7L, RoundingStrategies.floor()),
					Arguments.of("ceiling with zero scalar", 7L, RoundingStrategies.ceiling()),
					Arguments.of("truncate with zero scalar", 7L, RoundingStrategies.truncate())
			);
		}
	}
}

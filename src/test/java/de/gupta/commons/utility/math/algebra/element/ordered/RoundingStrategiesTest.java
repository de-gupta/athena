package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RoundingStrategies")
final class RoundingStrategiesTest
{
	private static void assertDivision(final String as, final long dividend, final long divisor,
	                                   final RoundingStrategy<IntegersAsEuclideanDomain> strategy,
	                                   final long expectedQuotient, final long expectedRemainder)
	{
		DivisionResult<IntegersAsEuclideanDomain> result = strategy.divide(integer(dividend), integer(divisor));
		assertThat(result.quotient()).as("%s: quotient", as).isEqualTo(integer(expectedQuotient));
		assertThat(result.remainder()).as("%s: remainder", as).isEqualTo(integer(expectedRemainder));
	}

	private static IntegersAsEuclideanDomain integer(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when using structure-based factories")
	final class WhenUsingStructureBasedFactories
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("structureFactoryCases")
		@DisplayName("produce the same results as the element-side strategies")
		void produceTheSameResultsAsTheElementSideStrategies(final String as,
		                                                     final RoundingStrategy<IntegersAsEuclideanDomain> actual,
		                                                     final RoundingStrategy<IntegersAsEuclideanDomain> expected,
		                                                     final long dividend, final long divisor)
		{
			assertThat(actual.divide(integer(dividend), integer(divisor))).as("%s: structure strategy result", as)
			                                                              .isEqualTo(expected.divide(integer(dividend),
																				  integer(divisor)));
		}

		private static Stream<Arguments> structureFactoryCases()
		{
			return Stream.of(
					Arguments.of("floor(structure)", RoundingStrategies.floor(IntegerEuclideanDomainStructure.INSTANCE),
							RoundingStrategies.floor(), -7L, 3L),
					Arguments.of("ceiling(structure)",
							RoundingStrategies.ceiling(IntegerEuclideanDomainStructure.INSTANCE),
							RoundingStrategies.ceiling(), 7L, 3L),
					Arguments.of("truncate(structure)",
							RoundingStrategies.truncate(IntegerEuclideanDomainStructure.INSTANCE),
							RoundingStrategies.truncate(), -7L, 3L)
			);
		}
	}

	@Nested
	@DisplayName("when using customizable factories")
	final class WhenUsingCustomizableFactories
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("customCeilingCases")
		@DisplayName("ceiling(floorStrategy, ...) matches ceiling semantics for exact and inexact division")
		void ceilingMatchesCeilingSemanticsForExactAndInexactDivision(final String as, final long dividend,
		                                                              final long divisor, final long expectedQuotient,
		                                                              final long expectedRemainder)
		{
			assertDivision(as, dividend, divisor, customCeiling(), expectedQuotient, expectedRemainder);
		}

		private static RoundingStrategy<IntegersAsEuclideanDomain> customCeiling()
		{
			return RoundingStrategies.ceiling(RoundingStrategies.floor(), IntegersAsEuclideanDomain::isZero,
					integer(1), IntegersAsEuclideanDomain::add, IntegersAsEuclideanDomain::subtract);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("customTruncateCases")
		@DisplayName("truncate(floorStrategy, ...) matches truncation semantics for exact and inexact division")
		void truncateMatchesTruncationSemanticsForExactAndInexactDivision(final String as, final long dividend,
		                                                                  final long divisor,
		                                                                  final long expectedQuotient,
		                                                                  final long expectedRemainder)
		{
			assertDivision(as, dividend, divisor, customTruncate(), expectedQuotient, expectedRemainder);
		}

		private static RoundingStrategy<IntegersAsEuclideanDomain> customTruncate()
		{
			return RoundingStrategies.truncate(RoundingStrategies.floor(), IntegersAsEuclideanDomain::isZero,
					IntegersAsEuclideanDomain::isNegative, integer(1), IntegersAsEuclideanDomain::add,
					IntegersAsEuclideanDomain::subtract);
		}

		private static Stream<Arguments> customCeilingCases()
		{
			return Stream.of(
					Arguments.of("inexact positive quotient", 7L, 3L, 3L, -2L),
					Arguments.of("inexact negative quotient", -7L, 3L, -2L, -1L),
					Arguments.of("exact quotient keeps floor result", 6L, 3L, 2L, 0L)
			);
		}

		private static Stream<Arguments> customTruncateCases()
		{
			return Stream.of(
					Arguments.of("positive quotient keeps floor result", 7L, 3L, 2L, 1L),
					Arguments.of("negative quotient adjusts toward zero", -7L, 3L, -2L, -1L),
					Arguments.of("exact quotient keeps floor result", 6L, 3L, 2L, 0L)
			);
		}
	}
}
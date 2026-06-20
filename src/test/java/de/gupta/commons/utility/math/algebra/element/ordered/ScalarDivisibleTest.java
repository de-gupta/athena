package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.IntegersAsEuclideanDomain;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScalarDivisible")
final class ScalarDivisibleTest
{
	private void assertBothSides(final long dividend, final long scalar,
	                             final RoundingStrategy<IntegersAsEuclideanDomain> strategy,
	                             final long expectedQ, final long expectedR)
	{
		DivisionResult<IntegersAsEuclideanDomain> element = e(dividend).divide(scalar, strategy);
		assertThat(element.quotient()).as("element quotient").isEqualTo(e(expectedQ));
		assertThat(element.remainder()).as("element remainder").isEqualTo(e(expectedR));

		DivisionResult<IntegersAsEuclideanDomain> structure =
				IntegerEuclideanDomainStructure.INSTANCE.divide(e(dividend), scalar, strategy);
		assertThat(structure.quotient()).as("structure quotient").isEqualTo(e(expectedQ));
		assertThat(structure.remainder()).as("structure remainder").isEqualTo(e(expectedR));
	}

	private static IntegersAsEuclideanDomain e(final long value)
	{
		return IntegersAsEuclideanDomain.of(value);
	}

	@Nested
	@DisplayName("when rounding with floor")
	final class WhenRoundingWithFloor
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesFloorQuotientAndRemainderCases")
		@DisplayName("produces floor quotient and non-negative remainder")
		void producesFloorQuotientAndRemainder(final String as, final long dividend, final long scalar,
		                                       final long expectedQ, final long expectedR)
		{
			assertBothSides(dividend, scalar, RoundingStrategies.floor(), expectedQ, expectedR);
		}

		private static Stream<Arguments> producesFloorQuotientAndRemainderCases()
		{
			return Stream.of(
					Arguments.of("420 ÷ 20 (SMA average)", 420L, 20L, 21L, 0L),
					Arguments.of("100 ÷ 3", 100L, 3L, 33L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L, 2L),
					Arguments.of("-100 ÷ 3", -100L, 3L, -34L, 2L),
					Arguments.of("7 ÷ 3", 7L, 3L, 2L, 1L),
					Arguments.of("1 ÷ 1 (identity scalar)", 1L, 1L, 1L, 0L),
					Arguments.of("0 ÷ 5 (zero dividend)", 0L, 5L, 0L, 0L),
					Arguments.of("large scalar binary path", 1_000_000_001L, 1_000_000_000L, 1L, 1L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with ceiling")
	final class WhenRoundingWithCeiling
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesCeilingQuotientAndRemainderCases")
		@DisplayName("produces ceiling quotient adjusting remainder accordingly")
		void producesCeilingQuotientAndRemainder(final String as, final long dividend, final long scalar,
		                                         final long expectedQ, final long expectedR)
		{
			assertBothSides(dividend, scalar, RoundingStrategies.ceiling(), expectedQ, expectedR);
		}

		private static Stream<Arguments> producesCeilingQuotientAndRemainderCases()
		{
			return Stream.of(
					Arguments.of("100 ÷ 3", 100L, 3L, 34L, -2L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L, -1L),
					Arguments.of("-100 ÷ 3", -100L, 3L, -33L, -1L),
					Arguments.of("7 ÷ 3", 7L, 3L, 3L, -2L),
					Arguments.of("large scalar binary path", 1_000_000_001L, 1_000_000_000L, 2L, -999_999_999L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with truncate")
	final class WhenRoundingWithTruncate
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("producesTruncatedQuotientAndRemainderCases")
		@DisplayName("produces quotient truncated toward zero")
		void producesTruncatedQuotientAndRemainder(final String as, final long dividend, final long scalar,
		                                           final long expectedQ, final long expectedR)
		{
			assertBothSides(dividend, scalar, RoundingStrategies.truncate(), expectedQ, expectedR);
		}

		private static Stream<Arguments> producesTruncatedQuotientAndRemainderCases()
		{
			return Stream.of(
					Arguments.of("100 ÷ 3 (positive, no adjust)", 100L, 3L, 33L, 1L),
					Arguments.of("-7 ÷ 3 (negative, adjust)", -7L, 3L, -2L, -1L),
					Arguments.of("-100 ÷ 3 (negative, adjust)", -100L, 3L, -33L, -1L),
					Arguments.of("7 ÷ 3 (positive, no adjust)", 7L, 3L, 2L, 1L),
					Arguments.of("large scalar binary path", 1_000_000_001L, 1_000_000_000L, 1L, 1L)
			);
		}
	}

	@Nested
	@DisplayName("when division is exact")
	final class WhenDivisionIsExact
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("allStrategiesProduceZeroRemainderCases")
		@DisplayName("all strategies produce zero remainder and the same quotient")
		void allStrategiesProduceZeroRemainderAndTheSameQuotient(final String as, final long dividend,
		                                                         final long scalar, final long expectedQ)
		{
			for (RoundingStrategy<IntegersAsEuclideanDomain> strategy : List.<RoundingStrategy<IntegersAsEuclideanDomain>>of(
					RoundingStrategies.floor(), RoundingStrategies.ceiling(), RoundingStrategies.truncate()))
				assertBothSides(dividend, scalar, strategy, expectedQ, 0L);
		}

		private static Stream<Arguments> allStrategiesProduceZeroRemainderCases()
		{
			return Stream.of(
					Arguments.of("420 ÷ 20 (SMA average)", 420L, 20L, 21L),
					Arguments.of("100 ÷ 10", 100L, 10L, 10L),
					Arguments.of("-60 ÷ 3", -60L, 3L, -20L),
					Arguments.of("0 ÷ 7", 0L, 7L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("division identity")
	final class DivisionIdentity
	{
		@Test
		@DisplayName("dividend = quotient * scalar + remainder for all strategies and sign combinations")
		void dividendEqualsQuotientTimesScalarPlusRemainderForAllStrategiesAndSignCombinations()
		{
			long[] dividends = {7L, -7L, 100L, -100L, 420L, 1_000_000_001L};
			long[] scalars = {3L, 7L, 20L, 63L};

			for (long dividend : dividends)
				for (long scalar : scalars)
					for (RoundingStrategy<IntegersAsEuclideanDomain> strategy : List.<RoundingStrategy<IntegersAsEuclideanDomain>>of(
							RoundingStrategies.floor(), RoundingStrategies.ceiling(), RoundingStrategies.truncate()))
					{
						DivisionResult<IntegersAsEuclideanDomain> result = e(dividend).divide(scalar, strategy);
						assertThat(result.quotient().multiply(e(scalar)).add(result.remainder()))
								.as("dividend=%d, scalar=%d: q*s+r must equal dividend", dividend, scalar)
								.isEqualTo(e(dividend));
					}
		}
	}
}
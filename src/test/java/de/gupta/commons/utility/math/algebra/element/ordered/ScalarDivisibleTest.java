package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumberFactory;
import de.gupta.commons.utility.math.algebra.structure.ring.DivisionResult;
import de.gupta.commons.utility.math.algebra.structure.ring.standard.IntegerEuclideanDomainStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScalarDivisible")
final class ScalarDivisibleTest
{
	private static Stream<StrategyArg> strategyArgs()
	{
		return Stream.of(
				new StrategyArg("FLOOR", DivisionConventions.floor()),
				new StrategyArg("CEILING", DivisionConventions.ceiling()),
				new StrategyArg("TRUNCATE", DivisionConventions.truncate())
		);
	}

	private void assertBothSides(final long dividend, final long scalar,
	                             final DivisionConvention<IntegralNumber> strategy,
	                             final long expectedQ, final long expectedR)
	{
		DivisionResult<IntegralNumber> element = e(dividend).divide(scalar, strategy);
		assertThat(element.quotient()).as("element quotient").isEqualTo(e(expectedQ));
		assertThat(element.remainder()).as("element remainder").isEqualTo(e(expectedR));

		DivisionResult<IntegralNumber> structure =
				IntegerEuclideanDomainStructure.INSTANCE.shrink(e(dividend), scalar, strategy);
		assertThat(structure.quotient()).as("structure quotient").isEqualTo(e(expectedQ));
		assertThat(structure.remainder()).as("structure remainder").isEqualTo(e(expectedR));
	}

	private static IntegralNumber e(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	private record StrategyArg(String name, DivisionConvention<IntegralNumber> strategy)
	{
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
			assertBothSides(dividend, scalar, DivisionConventions.floor(), expectedQ, expectedR);
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
			assertBothSides(dividend, scalar, DivisionConventions.ceiling(), expectedQ, expectedR);
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
			assertBothSides(dividend, scalar, DivisionConventions.truncate(), expectedQ, expectedR);
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
		@ParameterizedTest(name = "{0} — {4}")
		@MethodSource("allStrategiesProduceZeroRemainderAndTheSameQuotientCases")
		@DisplayName("all strategies produce zero remainder and the same quotient")
		void allStrategiesProduceZeroRemainderAndTheSameQuotient(final String as, final long dividend,
		                                                         final long scalar, final long expectedQ,
		                                                         final String strategyName,
		                                                         final DivisionConvention<IntegralNumber> strategy)
		{
			assertBothSides(dividend, scalar, strategy, expectedQ, 0L);
		}

		private static Stream<Arguments> allStrategiesProduceZeroRemainderAndTheSameQuotientCases()
		{
			record ExactCase(String as, long dividend, long scalar, long expectedQ)
			{
			}
			return Stream.of(
					new ExactCase("420 ÷ 20 (SMA average)", 420L, 20L, 21L),
					new ExactCase("100 ÷ 10", 100L, 10L, 10L),
					new ExactCase("-60 ÷ 3", -60L, 3L, -20L),
					new ExactCase("0 ÷ 7", 0L, 7L, 0L)
			).flatMap(tc -> strategyArgs().map(sa ->
					Arguments.of(tc.as(), tc.dividend(), tc.scalar(), tc.expectedQ(), sa.name(), sa.strategy())));
		}
	}

	@Nested
	@DisplayName("division identity")
	final class DivisionIdentity
	{
		@ParameterizedTest(name = "{0} ÷ {1} — {2}")
		@MethodSource("dividendEqualsQuotientTimesScalarPlusRemainderForAllStrategiesAndSignCombinationsCases")
		@DisplayName("dividend = quotient * scalar + remainder")
		void dividendEqualsQuotientTimesScalarPlusRemainderForAllStrategiesAndSignCombinations(
				final long dividend, final long scalar, final String strategyName,
				final DivisionConvention<IntegralNumber> strategy)
		{
			DivisionResult<IntegralNumber> result = e(dividend).divide(scalar, strategy);
			assertThat(result.quotient().multiply(e(scalar)).add(result.remainder()))
					.as("dividend=%d, scalar=%d — %s: q*s+r must equal dividend", dividend, scalar, strategyName)
					.isEqualTo(e(dividend));
		}

		private static Stream<Arguments> dividendEqualsQuotientTimesScalarPlusRemainderForAllStrategiesAndSignCombinationsCases()
		{
			long[] dividends = {7L, -7L, 100L, -100L, 420L, 1_000_000_001L};
			long[] scalars = {3L, 7L, 20L, 63L};
			return Arrays.stream(dividends).boxed()
			             .flatMap(dividend -> Arrays.stream(scalars).boxed()
			                                        .flatMap(scalar -> strategyArgs()
															.map(sa -> Arguments.of(dividend, scalar, sa.name(),
							                                        sa.strategy()))));
		}
	}
}
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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DivisionConvention")
final class EuclideanDivisionConventionTest
{
	private static Stream<StrategyArg> strategyArgs()
	{
		return Stream.of(
				new StrategyArg("FLOOR", DivisionConventions.floor()),
				new StrategyArg("CEILING", DivisionConventions.ceiling()),
				new StrategyArg("TRUNCATE", DivisionConventions.truncate())
		);
	}

	private void assertDivision(final String as, final long dividend, final long divisor,
	                            final DivisionConvention<IntegralNumber> strategy,
	                            final long expectedQuotient, final long expectedRemainder)
	{
		DivisionResult<IntegralNumber> element = e(dividend).divide(e(divisor), strategy);
		assertThat(element.quotient()).as("%s element quotient", as).isEqualTo(e(expectedQuotient));
		assertThat(element.remainder()).as("%s element remainder", as).isEqualTo(e(expectedRemainder));

		DivisionResult<IntegralNumber> structure =
				IntegerEuclideanDomainStructure.INSTANCE.divide(e(dividend), e(divisor), strategy);
		assertThat(structure.quotient()).as("%s structure quotient", as).isEqualTo(e(expectedQuotient));
		assertThat(structure.remainder()).as("%s structure remainder", as).isEqualTo(e(expectedRemainder));
	}

	private static IntegralNumber e(final long value)
	{
		return IntegralNumberFactory.of(value);
	}

	private record StrategyArg(String name, DivisionConvention<IntegralNumber> strategy)
	{
	}

	@Nested
	@DisplayName("when rounding with floor()")
	final class WhenRoundingWithFloor
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("floorDivisionCases")
		@DisplayName("produces floor quotient and remainder")
		void producesFloorQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                       final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, DivisionConventions.floor(), expectedQ, expectedR);
		}

		private static Stream<Arguments> floorDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -3L, 2L),
					Arguments.of("7 ÷ -3", 7L, -3L, -3L, -2L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L, -1L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with ceiling()")
	final class WhenRoundingWithCeiling
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("ceilingDivisionCases")
		@DisplayName("produces ceiling quotient and remainder")
		void producesCeilingQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                         final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, DivisionConventions.ceiling(), expectedQ, expectedR);
		}

		private static Stream<Arguments> ceilingDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 3L, -2L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L, -1L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L, 1L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 3L, 2L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
			);
		}
	}

	@Nested
	@DisplayName("when rounding with truncate()")
	final class WhenRoundingWithTruncate
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("truncateDivisionCases")
		@DisplayName("produces truncated quotient and remainder")
		void producesTruncatedQuotientAndRemainder(final String as, final long dividend, final long divisor,
		                                           final long expectedQ, final long expectedR)
		{
			assertDivision(as, dividend, divisor, DivisionConventions.truncate(), expectedQ, expectedR);
		}

		private static Stream<Arguments> truncateDivisionCases()
		{
			return Stream.of(
					Arguments.of("7 ÷ 3", 7L, 3L, 2L, 1L),
					Arguments.of("-7 ÷ 3", -7L, 3L, -2L, -1L),
					Arguments.of("7 ÷ -3", 7L, -3L, -2L, 1L),
					Arguments.of("-7 ÷ -3", -7L, -3L, 2L, -1L),
					Arguments.of("6 ÷ 3", 6L, 3L, 2L, 0L)
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
		                                                         final long divisor, final long expectedQ,
		                                                         final String strategyName,
		                                                         final DivisionConvention<IntegralNumber> strategy)
		{
			assertDivision(as, dividend, divisor, strategy, expectedQ, 0L);
		}

		private static Stream<Arguments> allStrategiesProduceZeroRemainderAndTheSameQuotientCases()
		{
			record ExactCase(String as, long dividend, long divisor, long expectedQ)
			{
			}
			return Stream.of(
					new ExactCase("6 ÷ 3", 6L, 3L, 2L),
					new ExactCase("-6 ÷ 3", -6L, 3L, -2L),
					new ExactCase("6 ÷ -3", 6L, -3L, -2L),
					new ExactCase("-6 ÷ -3", -6L, -3L, 2L)
			).flatMap(tc -> strategyArgs().map(sa ->
					Arguments.of(tc.as(), tc.dividend(), tc.divisor(), tc.expectedQ(), sa.name(), sa.strategy())));
		}
	}

	@Nested
	@DisplayName("division identity")
	final class DivisionIdentity
	{
		@ParameterizedTest(name = "{0} — {3}")
		@MethodSource("dividendEqualsQuotientTimesDivisorPlusRemainderCases")
		@DisplayName("dividend = quotient * divisor + remainder")
		void dividendEqualsQuotientTimesDivisorPlusRemainder(final String as, final long dividend,
		                                                     final long divisor, final String strategyName,
		                                                     final DivisionConvention<IntegralNumber> strategy)
		{
			DivisionResult<IntegralNumber> result = e(dividend).divide(e(divisor), strategy);
			assertThat(result.quotient().multiply(e(divisor)).add(result.remainder()))
					.as("%s — %s: dividend = q*d + r", as, strategyName).isEqualTo(e(dividend));
		}

		private static Stream<Arguments> dividendEqualsQuotientTimesDivisorPlusRemainderCases()
		{
			record DivCase(String as, long dividend, long divisor)
			{
			}
			return Stream.of(
					new DivCase("7 ÷ 3", 7L, 3L),
					new DivCase("-7 ÷ 3", -7L, 3L),
					new DivCase("7 ÷ -3", 7L, -3L),
					new DivCase("-7 ÷ -3", -7L, -3L),
					new DivCase("6 ÷ 3", 6L, 3L)
			).flatMap(tc -> strategyArgs().map(sa ->
					Arguments.of(tc.as(), tc.dividend(), tc.divisor(), sa.name(), sa.strategy())));
		}
	}
}